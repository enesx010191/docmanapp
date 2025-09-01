package com.moneymate.documentationManagement.core.utilities.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.moneymate.documentationManagement.core.utilities.exceptions.Results.DataResult;
import com.moneymate.documentationManagement.core.utilities.exceptions.Results.Result;


public class ResponseEntityBuilder {


	// Sadece mesaj içeren Result nesneleri için (örneğin: kayıt, silme, güncelleme)
    public static ResponseEntity<Result> fromResult(Result result) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    // Veri taşıyan DataResult nesneleri için (örneğin: getAll, getById)
    public static <T> ResponseEntity<DataResult<T>> fromDataResult(DataResult<T> result) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    // Yeni bir kaynak oluşturulduğunda (örneğin: POST /users/add)
    public static <T> ResponseEntity<DataResult<T>> created(DataResult<T> result) {
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // Başarılı silme/güncelleme işlemi sonrası içerik dönmek istemediğinde
    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }
}



