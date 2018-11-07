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
    private int coefficient;

    /**
     * Initializes coefficient.
     *
     * @param coefficient Coefficient for converting into bytes
     */
    FileSizeUnit(int coefficient) {
        this.coefficient = coefficient;
    }

    public int getCoefficient() {
        return coefficient;
    }
}
