package third.elastic.userDocument;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 用户索引文档
 *
 * @author dongxiaozhe-hj
 * @date 2020/12/12
 */
@Data
public class UserDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    public UserDocument() {
    }

    public UserDocument(Long uid) {
        this.uid = uid;
    }

    /**
     * 主键
     */
    private Long uid;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 签名
     */
    private String signature;
    /**
     * 性别
     */
    private String gender;
    /**
     * 星座
     */
    private String astro;
    /**
     * 位置
     */
    private String location;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 最后修改时间
     */
    private Date updatedAt;

    /**
     * 等级
     */
    private Integer level;
    /**
     * 魅力等级
     */
    private Integer charmLevel;

    /**
     * 动态数
     */
    private Long feeds;
    /**
     * 观看数
     */
    private Long watches;
    /**
     * 粉丝数
     */
    private Long followers;
    /**
     * 关注数
     */
    private Long followings;

    /**
     * 认证状态：0-未认证，1-个人，2-团体
     */
    private Integer verifyType;
    /**
     * 认证标签
     */
    private Set<String> verifyLabel;
    /**
     * 真实姓名
     */
    private String verifyRealname;
    /**
     * 认证信息
     */
    private String verifyCredentials;
    /**
     * 人工排序，大在前
     */
    private Integer verifyRank;

    /**
     * 是否在线
     */
    private Boolean online;
    /**
     * 直播列表ID
     */
    private List<Long> liveIds;

    /**
     * 标签
     */
    private List<String> tags;
    /**
     * 运营关键字
     */
    private String opKeywords;

}
