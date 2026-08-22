package jp.co.sss.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "prize")
public class Prize {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_prize_gen")
    @SequenceGenerator(name = "seq_prize_gen", sequenceName = "seq_prize", allocationSize = 1)
    private Integer id;

    @Column
    private String name;

    @Column(name = "required_point")
    private Integer requiredPoint;

    @Column
    private String image;

    @Column
    private String description;
    
    @Column(name = "delete_flag")
    private Integer deleteFlag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRequiredPoint() {
        return requiredPoint;
    }

    public void setRequiredPoint(Integer requiredPoint) {
        this.requiredPoint = requiredPoint;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}