package ucl.group.excelSystem.api.db.dao;

import org.apache.ibatis.annotations.*;
import ucl.group.excelSystem.api.db.pojo.BasicPartnerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface PartnerDao {
//    遍历
    @Results({
            @Result(column = "partner_company_id", property = "partnerCompanyId"),
            @Result(column = "company_name", property = "partnerCompanyName"),
            @Result(column = "postalCode", property = "partnerpostalCode"),
            @Result(column = "address", property = "partnerCompanyAddress"),
            @Result(column = "representitive_name", property = "representativeName"),
            @Result(column = "terms_of_payment", property = "termsofPayment"),
            @Result(column = "coop_date", property = "coopDate"),
    })
    @Select("select * from basic_v2_partner_company")
    List<BasicPartnerEntity> getAllPartners();
//    根据名字查询

    @Select("select partner_company_id, company_name,postalCode,address,representitive_name,terms_of_payment,coop_date from basic_v2_partner_company where company_name=#{partnerCompanyName}")
    @Results({
            @Result(column = "partner_company_id", property = "partnerCompanyId"),
            @Result(column = "company_name", property = "partnerCompanyName"),
            @Result(column = "postalCode", property = "partnerpostalCode"),
            @Result(column = "address", property = "partnerCompanyAddress"),
            @Result(column = "representitive_name", property = "representativeName"),
            @Result(column = "terms_of_payment", property = "termsofPayment"),
            @Result(column = "coop_date", property = "coopDate"),
    })
    public BasicPartnerEntity getPartnerByName(String partnerCompanyName);

//    增加
    @Insert("INSERT INTO basic_v2_partner_company (partner_company_id, company_name,postalCode,address,representitive_name,terms_of_payment,coop_date) VALUES (#{partnerCompanyId}, #{partnerCompanyName},#{partnerpostalCode},#{partnerCompanyAddress},#{representativeName},#{termsofPayment},#{coopDate})")
    public void addPartner(BasicPartnerEntity basicPartnerEntity);


    //      改
    @Update("update basic_v2_partner_company set company_name=#{partnerCompanyName},postalCode=#{partnerpostalCode},address=#{partnerCompanyAddress},representitive_name=#{representativeName},terms_of_payment=#{termsofPayment},coop_date=#{coopDate} where partner_company_id=#{partnerCompanyId}")
    public void updatePartner(BasicPartnerEntity basicPartnerEntity);
//    删
    @Delete({
        "<script>",
        "DELETE FROM basic_v2_partner_company WHERE partner_company_id IN",
        "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
        "#{id}",
        "</foreach>",
        "</script>"
    })
    public void deletePartnerById(@Param("ids") List<Integer> companyids);

//    分页查询

    @Select({
            "<script>",
            "SELECT",
            "    partner_company_id AS partnerCompanyId,",
            "    company_name AS partnerCompanyName,",
            "    postalCode AS partnerPostalCode,",
            "    address AS partnerCompanyAddress,",
            "    representitive_name AS representativeName,",
            "    terms_of_payment AS termsOfPayment,",
            "    coop_date AS coopDate,",
            "    remark",
            "FROM basic_v2_partner_company",
            "<where>",
            "    <if test='param.partnerCompanyName != null and param.partnerCompanyName != \"\"'>",
            "        AND company_name LIKE CONCAT('%', #{param.partnerCompanyName}, '%')",
            "    </if>",
            "</where>",
            "LIMIT #{param.length} OFFSET #{param.start}",
            "</script>"
    })
    List<Map<String, Object>> selectCompanyByPage(@Param("param") Map<String, Object> param);

    @Select("SELECT COUNT(*) FROM basic_v2_partner_company")

    public Long selectCompanyByPageCount();
    @Select({
            "<script>",
            "SELECT",
            "    MIN(partner_company_id) AS partnerCompanyId,",  // 使用 MIN 选择任意一条数据
            "    company_name AS partnerCompanyName,",
            "    MIN(remark) AS remark",  // 可以选择其他列的最小值
            "FROM",
            "    basic_v2_partner_company",
            "GROUP BY company_name",  // 按 company_name 分组去重
            "</script>"
    })
    ArrayList<HashMap> selectCompany();


}

