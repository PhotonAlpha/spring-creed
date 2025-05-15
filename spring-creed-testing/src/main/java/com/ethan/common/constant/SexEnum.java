package com.ethan.common.constant;

public enum SexEnum implements PersistEnum2DB<Integer> {
    /** 男 */
    MALE(1),
    /** 女 */
    FEMALE(2),
    /* 未知 */
    UNKNOWN(3);
    /**
     * 性别
     */
    private Integer sex;

    SexEnum(Integer sex) {
        this.sex = sex;
    }

    public Integer getSex() {
        return sex;
    }

    @Override
    public Integer getData() {
        return sex;
    }

    public static class Converter extends AbstractEnumConverter<SexEnum, Integer> {
        public Converter() {
            super(SexEnum.class);
        }
    }
}
