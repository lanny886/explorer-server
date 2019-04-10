package com.xyz.browser.common.enums;

/**
 * 消息类型
 *
 */
public enum WsMsgTypeEnum {
    LATEST_BLOCK("实时block", "LATEST_BLOCK"),
//    LATEST_TXN_STATUS("实时transaction状态","LATEST_TXN_STATUS"),
//    TOTAL_TXN_COUNTER("实时transaction计数", "TOTAL_TXN_COUNTER"),
    LATEST_CONTRACT("实时contract", "LATEST_CONTRACT")

    ;

    /**
     * The Type.
     */
    private String type;
    /**
     * The Name.
     */
    private String name;

    WsMsgTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets name.
     *
     * @param type the type
     *
     * @return the name
     */
    public static String getName(Integer type) {
        for (WsMsgTypeEnum ele : WsMsgTypeEnum.values()) {
            if (type.equals(ele.getType())) {
                return ele.getName();
            }
        }
        return null;
    }

    /**
     * Gets enum.
     *
     * @param type the type
     *
     * @return the enum
     */
    public static WsMsgTypeEnum getEnum(Integer type) {
        for (WsMsgTypeEnum ele : WsMsgTypeEnum.values()) {
            if (type.equals(ele.getType())) {
                return ele;
            }
        }
        return null;
    }
}