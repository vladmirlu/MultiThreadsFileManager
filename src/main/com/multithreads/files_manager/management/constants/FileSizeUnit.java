package com.multithreads.files_manager.management.constants;

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
    private long coefficient;

    /**
     * Initializes coefficient.
     *
     * @param coefficient Coefficient for converting into bytes
     */
    FileSizeUnit(long coefficient) {
        this.coefficient = coefficient;
    }

    public long getCoefficient() {
        return coefficient;
    }

    public static FileSizeUnit getFileSizeUnit(long fileSplitLength){
        FileSizeUnit sizeUnit = BYTE;
        for(FileSizeUnit fileSizeUnit: FileSizeUnit.values()){
            if(fileSplitLength / fileSizeUnit.coefficient >= 1){
                sizeUnit = fileSizeUnit;
            }
        }
        return sizeUnit;
    }
}
