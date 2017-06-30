package SwePub;

/**
 * Created by Cristian on 2016-10-13.
 */
public class ClassificationCategory {



    private int level;
    private Integer code;
    private String swe_description;
    private String eng_description;


    public ClassificationCategory(Integer code, int level, String eng_name, String swe_name) {

        this.level = level;
        this.code = code;
        this.swe_description = swe_name;
        this.eng_description = eng_name;

    }

    public int getLevel() {
        return level;
    }



    public int getCode() {
        return code;
    }



    public String getSwe_description() {
        return swe_description;
    }



    public String getEng_description() {
        return eng_description;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassificationCategory that = (ClassificationCategory) o;

        if (level != that.level) return false;
        if (code != that.code) return false;
        if (!swe_description.equals(that.swe_description)) return false;
        return eng_description.equals(that.eng_description);

    }

    @Override
    public int hashCode() {
        int result = level;
        result = 31 * result + code;
        result = 31 * result + swe_description.hashCode();
        result = 31 * result + eng_description.hashCode();
        return result;
    }


    @Override
    public String toString() {
        return "ClassificationCategory{" +
                "level=" + level +
                ", code=" + code +
                ", swe_description='" + swe_description + '\'' +
                ", eng_description='" + eng_description + '\'' +
                '}';
    }
}
