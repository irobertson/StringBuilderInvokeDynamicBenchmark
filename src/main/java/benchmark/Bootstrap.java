package benchmark;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

/**
 * Pair of bootstrap methods needed for accessing potentially private fields and methods
 */
public class Bootstrap {
  /**
   * InvokeDynamic bootstrap method to create a field-read call site for the field "s" in {@link Bean}.
   *
   * @param lookup The lookup context, used for getting a MethodHandle
   * @param name the "name" of our "method" - unused
   * @param methodType signature of the method/opcode we should return
   * @return a callsite
   */
  public static CallSite getField(MethodHandles.Lookup lookup, String name, MethodType methodType)
      throws IllegalAccessException, NoSuchFieldException, SecurityException {
    Field field = Bean.class.getDeclaredField("s");
    field.setAccessible(true);
    MethodHandle methodHandle = lookup.unreflectGetter(field);
    return new ConstantCallSite(methodHandle);
  }
}
