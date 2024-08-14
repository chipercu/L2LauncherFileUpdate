package com.fuzzy.model.file_edit.dat;

public class ItemNameDat {

    public static final String en = "itemname-e.dat";
    public static final String ru = "itemname-ru.dat";
    public static final String ddf = "itemname-e.ddf";

    private int id = 0;
    private String name="";
    private String add_name="";
    private String description="";
    private int popup = -1;
    private int supercnt0 = 0;
    private int cnt0 = 0;
    private String body_ids = "";
    private String legging_ids = "";
    private String helm_ids = "";
    private String glove_ids = "";
    private String boots_ids = "";
    private String set_bonus_desc = "a,";
    private int supercnt1 = 0;
    private int cnt1 = 0;
    private String set_extra_ids = "";
    private String set_extra_desc = "a,";
    private int unk1_0 = 0;
    private int unk1_1 = 0;
    private int unk1_2 = 0;
    private int unk1_3 = 0;
    private int unk1_4 = 0;
    private int unk1_5 = 0;
    private int unk1_6 = 0;
    private int unk1_7 = 0;
    private int unk1_8 = 0;
    private int special_enchant_amount = 0;
    private String special_enchant_desc = "a,";
    private int unk2 = 1;


    public ItemNameDat() {
    }




    @Override
    public String toString() {

        String string = id + "\t" + name+ "\t" + add_name+ "\t" + description+ "\t" + popup+ "\t" + supercnt0+ "\t" + cnt0+ "\t";

        return "itemname{}";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdd_name() {
        return add_name;
    }

    public void setAdd_name(String add_name) {
        this.add_name = add_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPopup() {
        return popup;
    }

    public void setPopup(int popup) {
        this.popup = popup;
    }

    public int getSupercnt0() {
        return supercnt0;
    }

    public void setSupercnt0(int supercnt0) {
        this.supercnt0 = supercnt0;
    }

    public int getCnt0() {
        return cnt0;
    }

    public void setCnt0(int cnt0) {
        this.cnt0 = cnt0;
    }

    public String getBody_ids() {
        return body_ids;
    }

    public void setBody_ids(String body_ids) {
        this.body_ids = body_ids;
    }

    public String getLegging_ids() {
        return legging_ids;
    }

    public void setLegging_ids(String legging_ids) {
        this.legging_ids = legging_ids;
    }

    public String getHelm_ids() {
        return helm_ids;
    }

    public void setHelm_ids(String helm_ids) {
        this.helm_ids = helm_ids;
    }

    public String getGlove_ids() {
        return glove_ids;
    }

    public void setGlove_ids(String glove_ids) {
        this.glove_ids = glove_ids;
    }

    public String getBoots_ids() {
        return boots_ids;
    }

    public void setBoots_ids(String boots_ids) {
        this.boots_ids = boots_ids;
    }

    public String getSet_bonus_desc() {
        return set_bonus_desc;
    }

    public void setSet_bonus_desc(String set_bonus_desc) {
        this.set_bonus_desc = set_bonus_desc;
    }

    public int getSupercnt1() {
        return supercnt1;
    }

    public void setSupercnt1(int supercnt1) {
        this.supercnt1 = supercnt1;
    }

    public int getCnt1() {
        return cnt1;
    }

    public void setCnt1(int cnt1) {
        this.cnt1 = cnt1;
    }

    public String getSet_extra_ids() {
        return set_extra_ids;
    }

    public void setSet_extra_ids(String set_extra_ids) {
        this.set_extra_ids = set_extra_ids;
    }

    public String getSet_extra_desc() {
        return set_extra_desc;
    }

    public void setSet_extra_desc(String set_extra_desc) {
        this.set_extra_desc = set_extra_desc;
    }

    public int getUnk1_0() {
        return unk1_0;
    }

    public void setUnk1_0(int unk1_0) {
        this.unk1_0 = unk1_0;
    }

    public int getUnk1_1() {
        return unk1_1;
    }

    public void setUnk1_1(int unk1_1) {
        this.unk1_1 = unk1_1;
    }

    public int getUnk1_2() {
        return unk1_2;
    }

    public void setUnk1_2(int unk1_2) {
        this.unk1_2 = unk1_2;
    }

    public int getUnk1_3() {
        return unk1_3;
    }

    public void setUnk1_3(int unk1_3) {
        this.unk1_3 = unk1_3;
    }

    public int getUnk1_4() {
        return unk1_4;
    }

    public void setUnk1_4(int unk1_4) {
        this.unk1_4 = unk1_4;
    }

    public int getUnk1_5() {
        return unk1_5;
    }

    public void setUnk1_5(int unk1_5) {
        this.unk1_5 = unk1_5;
    }

    public int getUnk1_6() {
        return unk1_6;
    }

    public void setUnk1_6(int unk1_6) {
        this.unk1_6 = unk1_6;
    }

    public int getUnk1_7() {
        return unk1_7;
    }

    public void setUnk1_7(int unk1_7) {
        this.unk1_7 = unk1_7;
    }

    public int getUnk1_8() {
        return unk1_8;
    }

    public void setUnk1_8(int unk1_8) {
        this.unk1_8 = unk1_8;
    }

    public int getSpecial_enchant_amount() {
        return special_enchant_amount;
    }

    public void setSpecial_enchant_amount(int special_enchant_amount) {
        this.special_enchant_amount = special_enchant_amount;
    }

    public String getSpecial_enchant_desc() {
        return special_enchant_desc;
    }

    public void setSpecial_enchant_desc(String special_enchant_desc) {
        this.special_enchant_desc = special_enchant_desc;
    }

    public int getUnk2() {
        return unk2;
    }

    public void setUnk2(int unk2) {
        this.unk2 = unk2;
    }
}
