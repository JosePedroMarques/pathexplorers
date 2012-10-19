package utils;

// TODO: Auto-generated Javadoc
/**
 * The Class Pair.
 *
 * @param <A> the generic type
 * @param <B> the generic type
 */
public class Pair<A, B> {
    
    /** The first. */
    private A first;
    
    /** The second. */
    private B second;

    /**
     * Instantiates a new pair.
     *
     * @param first the first
     * @param second the second
     */
    public Pair(A first, B second) {
        super();
        this.first = first;
        this.second = second;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;

        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("rawtypes")
	public boolean equals(Object other) {
        if (other instanceof Pair) {
                Pair otherPair = (Pair) other;
                return 
                ((  this.first == otherPair.first ||
                        ( this.first != null && otherPair.first != null &&
                          this.first.equals(otherPair.first))) &&
                 (      this.second == otherPair.second ||
                        ( this.second != null && otherPair.second != null &&
                          this.second.equals(otherPair.second))) );
        }

        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    { 
           return "(" + first + ", " + second + ")"; 
    }

    /**
     * Gets the first.
     *
     * @return the first
     */
    public A getFirst() {
        return first;
    }

    /**
     * Sets the first.
     *
     * @param first the new first
     */
    public void setFirst(A first) {
        this.first = first;
    }

    /**
     * Gets the second.
     *
     * @return the second
     */
    public B getSecond() {
        return second;
    }

    /**
     * Sets the second.
     *
     * @param second the new second
     */
    public void setSecond(B second) {
        this.second = second;
    }
}
