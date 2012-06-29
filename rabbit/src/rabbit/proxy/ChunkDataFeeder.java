package rabbit.proxy;

interface ChunkDataFeeder {
    /** The chunk reader needs more data.
     */
    void register ();

    /** The chunk reader needs to read more data, 
     *  compact buffer before registering.
     */
    void readMore ();

    /** Chunk reading has been completed. 
     */
    void finishedRead ();
}
