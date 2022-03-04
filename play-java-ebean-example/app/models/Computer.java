package models;

import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Computer entity managed by Ebean
 */
@Entity 
public class Computer extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Constraints.Required
    private String name;
    
    @Formats.DateTime(pattern="yyyy-MM-dd")
    private Date introduced;
    
    @Formats.DateTime(pattern="yyyy-MM-dd")
    private Date discontinued;
    
    @ManyToOne
    private Company company;

    public void update(Computer newComputerData) {
        setName(newComputerData.getName());
        setCompany(newComputerData.getCompany());
        setDiscontinued(newComputerData.getDiscontinued());
        setIntroduced(newComputerData.getIntroduced());
        update();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getIntroduced() {
        return introduced;
    }

    public void setIntroduced(Date introduced) {
        this.introduced = introduced;
    }

    public Date getDiscontinued() {
        return discontinued;
    }

    public void setDiscontinued(Date discontinued) {
        this.discontinued = discontinued;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}

