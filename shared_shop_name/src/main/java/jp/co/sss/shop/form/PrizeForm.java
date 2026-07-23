package jp.co.sss.shop.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

public class PrizeForm {

    private Integer id;


    @NotBlank(message = "{prize.name.required}")
    private String name;

    @NotNull(message = "{prize.requiredPoint.required}")
    @Min(value = 1, message = "{prize.requiredPoint.min}")
    private Integer requiredPoint;

    @NotBlank(message = "{prize.description.required}")
    private String description;

    private String image;

    private MultipartFile imageFile;
    
    
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


	public MultipartFile getImageFile() {
	    return imageFile;
	}

	public void setImageFile(MultipartFile imageFile) {
	    this.imageFile = imageFile;
	}
    
}
