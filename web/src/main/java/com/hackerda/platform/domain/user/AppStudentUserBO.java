package com.hackerda.platform.domain.user;

import com.hackerda.platform.domain.student.StudentAccount;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class AppStudentUserBO extends AppUserBO{

    private final StudentAccount account;

    public AppStudentUserBO(StudentAccount studentAccount, String nickname, String password, String avatarPath, PhoneNumber mobile,
                            Gender gender, String introduction) {
        super(studentAccount.getAccount(), nickname, password, avatarPath, mobile, gender, introduction);
        super.setUserType(UserType.Student);
        super.setUseDefaultPassword(true);
        this.account = studentAccount;
    }

    public AppStudentUserBO(StudentAccount studentAccount,String userName, String nickname, String password,
                            String salt, String avatarPath,
                            PhoneNumber mobile,
                            Gender gender, String introduction, boolean useDefaultPassword) {
        super(userName, nickname, password, salt, avatarPath, mobile, gender, introduction, useDefaultPassword);
        super.setUserType(UserType.Student);

        this.account = studentAccount;
    }

    @Override
    public String toString() {
        return "AppStudentUserBO{" +
                "account=" + account +
                '}' + "AppUserBO{ "+ super.toString() + "}" ;
    }

    public StudentAccount getAccount() {
        return account;
    }



}