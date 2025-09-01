package com.moneymate.documentationManagement.core.utilities;

public class Messages {

    // ✅ Başarılı işlemler
    public static final String UserCreated = "Kullanıcı başarıyla eklendi.";
    public static final String UserUpdated = "Kullanıcı başarıyla güncellendi.";

    public static final String OperationSuccess = "İşlem başarılı.";
    public static final String OperationFailed = "Bir hata oluştu.";

    // ✅ Business Exception mesajları
    public static final String ExistsPhoneNumber = "Bu telefon numarası ile kayıtlı bir kullanıcı zaten var.";

    // ✅ User Validation mesajları
    public static final String FirstNameNotValid = "İsim 2 ile 50 karakter arasında olmalıdır.";
    public static final String LastNameNotValid = "Soyisim 2 ile 50 karakter arasında olmalıdır.";
    public static final String EmailNotValid = "Geçerli bir email adresi giriniz (en fazla 100 karakter).";
    public static final String BirthdayNotValid = "Doğum tarihi formatı geçersizdir. (gg-aa-yyyy)";
    public static final String PhoneNumberNotValid = "Telefon numarası 10-15 haneli olmalıdır.";
    public static final String PasswordNotValid = "Şifre boş olamaz.";

    public static final String UserIdMustBePositive = "Kullanıcı ID pozitif bir sayı olmalıdır.";
    public static final String AlreadyExistUser = "Mevcut Kullanıcı.";
    public static final String UserIdRequired = "Kullanıcı ID Boş olamaz.";
    public static final String UserNotFound = "Kullanıcı bulunamadı.";
    public static final String UserDeleted = "Kullanıcı başarıyla silindi.";

    public static final String LoginSuccessful = "Kullanıcı giriş başarılı";
    public static final String LoginInformationIsIncorrect = "Kullanıcı giriş bilgileri yanlış";

    // ✅ Document Operations
    public static final String DocumentAdded = "Doküman başarıyla eklendi";
    public static final String GetByIdDocument = "Doküman başarıyla getirildi";
    public static final String GetByAllDocument = "Tüm dokümanlar getirildi";
    public static final String DocumentsGetSuccess = "Dokümanlar başarıyla getirildi";
    public static final String DocumentUpdateSuccess = "Doküman başarıyla güncellendi";
    public static final String DocumentsDeletedSuccess = "Doküman başarıyla silindi";
    public static final String DocumentsDeletedFailed = "Doküman bulunamadı";
    public static final String DocumentsFindFailed = "Doküman bulunamadı";

    // ✅ Document Validation mesajları
    public static final String DocumentTitleNotValid = "Döküman başlığı 2 ile 200 karakter arasında olmalıdır.";
    public static final String InstitutionNameNotValid = "Kurum adı 2 ile 100 karakter arasında olmalıdır.";
    public static final String InstitutionTypeNotValid = "Kurum tipi 2 ile 50 karakter arasında olmalıdır.";
    public static final String InstitutionUrlNotValid = "Geçerli bir URL adresi giriniz.";
    public static final String DocumentTypeNotValid = "Döküman tipi boş olamaz.";
    public static final String DocumentDescriptionNotValid = "Döküman açıklaması en fazla 500 karakter olmalıdır.";
    public static final String FileNotValid = "Lütfen geçerli bir dosya seçiniz.";
    public static final String FileSizeNotValid = "Dosya boyutu 10MB'dan büyük olamaz.";
    public static final String SearchTitleNotValid = "Arama başlığı en az 2 karakter olmalıdır.";
}