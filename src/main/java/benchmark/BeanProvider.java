package benchmark;

public enum BeanProvider {
  NEVER_NULL {
    @Override
    public Bean getBean() {
      return BEAN_WITH_NON_NULL;
    }
  }, SOMETIMES_NULL {
    int count = 0;
    @Override
    public Bean getBean() {
      return ((count++ % 100) == 0) ? BEAN_WITH_NULL : BEAN_WITH_NON_NULL;
    }
  };

  private final static Bean BEAN_WITH_NON_NULL = new Bean("foo");
  private final static Bean BEAN_WITH_NULL = new Bean(null);

  public abstract Bean getBean();

}
