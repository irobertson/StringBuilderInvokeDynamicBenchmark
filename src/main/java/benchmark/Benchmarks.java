package benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
// Hotspot seems to do all its optimizations pretty quickly, so let's save some time.
@Fork(value = 1, warmups = 0)
@Measurement(iterations=5)
@Warmup(iterations = 5)
public class Benchmarks {
  @Param({"false", "true"})
  public boolean useInvokeDynamic;

  @Param({"0", "1", "10", "100"})
  public int constStringAppendCount;

  @Param({"NEVER_NULL", "SOMETIMES_NULL"})
  public BeanProvider provider;

  private BeanMarshaller beanMarshaller;

  @Setup
  public void makeMarshaller()  {
    try {
      beanMarshaller = (BeanMarshaller) new ByteClassLoader(Bean.class.getClassLoader()).loadClass(
      "benchmark.BeanMarshallerImpl",
      BeanMarshallerImplGenerator.dump(constStringAppendCount, useInvokeDynamic)).newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Benchmark
  public String benchmarkMarshal() throws Exception {
    return beanMarshaller.marshal(provider.getBean());
  }
}
