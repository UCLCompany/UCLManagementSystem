package ucl.group.excelSystem.api.db.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationThreeVO {
    private Long organizationOneId;

    private Long organizationTwoId;

    private Long organizationThreeId;

    private Long RelatedOrg1AndOrg2Id;

    private Long RelatedOrg2AndOrg3Id;

    private String organizationName;

    private String belong;

    //组织1名字
    private String organizationOneName;

    //组织1的所属
    private String organizationOneBelong;

    private String organizationTwoName;

    private String organizationTwoBelong;
}
