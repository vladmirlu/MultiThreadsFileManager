package com.multithreads.management.constants;

/**
 * Available size units (size types)
 */
public enum FileSizeUnit {

    /**
     * Byte.
     */
    BYTE(1),

    /**
     * Kilobyte.
     */
    KILOBYTE(1000),

    /**
     * Megabyte.
     */
    MEGABYTE(1000000),

    /**
     * Megabyte.
     */
    GIGABYTE(1000000000);

    /**
     * Coefficient for converting into bytes.
     */
    private final long coefficient;

    /**
     * Buffer size.
     */
    public static final int BUFFER_SIZE =  8 * 1024;

    /**
     * Initializes coefficient.
     *
     * @param coefficient Coefficient for converting into bytes
     */
    FileSizeUnit(long coefficient) {
        this.coefficient = coefficient;
    }

    public static long getSpecificBufferSize(long fileSplitLength){
        FileSizeUnit sizeUnit = BYTE;
        for(FileSizeUnit fileSizeUnit: FileSizeUnit.values()){
            if(fileSplitLength / fileSizeUnit.coefficient >= 1){
                sizeUnit = fileSizeUnit;
            }
        }
        return BUFFER_SIZE * sizeUnit.coefficient;
    }
}
