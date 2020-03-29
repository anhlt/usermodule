package models.services
import forms.SignUpForm

trait UserRegisterService {

    def userRegister(data: SignUpForm.Data): Boolean
}