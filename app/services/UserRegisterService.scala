package services
import forms.SignUpForm
import com.google.inject._


trait UserRegisterService {

    def userRegister(data: SignUpForm.Data): Boolean
}