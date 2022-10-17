package kashyap.anurag.frenzystore.Models;

import android.widget.ImageView;

public class AddressModel {

    private String name, area, pinCode, state, landmark, phoneNo, city;
    private ImageView checkedBtn, moreBtn;
    private Boolean selected;

    public AddressModel() {
    }

    public AddressModel(String name, String area, String pinCode, String state, String landmark, String phoneNo, String city, ImageView checkedBtn, ImageView moreBtn, Boolean selected) {
        this.name = name;
        this.area = area;
        this.pinCode = pinCode;
        this.state = state;
        this.landmark = landmark;
        this.phoneNo = phoneNo;
        this.city = city;
        this.checkedBtn = checkedBtn;
        this.moreBtn = moreBtn;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ImageView getCheckedBtn() {
        return checkedBtn;
    }

    public void setCheckedBtn(ImageView checkedBtn) {
        this.checkedBtn = checkedBtn;
    }

    public ImageView getMoreBtn() {
        return moreBtn;
    }

    public void setMoreBtn(ImageView moreBtn) {
        this.moreBtn = moreBtn;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
