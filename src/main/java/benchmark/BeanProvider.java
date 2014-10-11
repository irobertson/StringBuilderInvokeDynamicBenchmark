package benchmark;

public enum BeanProvider {
  /**
   * Always return a bean where the field s is non-null.
   */
  NEVER_NULL {
    @Override
    public Bean getBean() {
      return BEAN_WITH_NON_NULL;
    }
  },
  /**
   * Every 100 times, return a Bean where the field s is null; all other times, return one where s is non-null.
   */
  SOMETIMES_NULL {
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
