package com.backend.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.backend.model.UploadedFile;

public class FileValidator implements Validator {

	@Override
	public void validate(Object uploadedFile, Errors errors) {

		UploadedFile file = (UploadedFile) uploadedFile;

		if (file.getFile().getSize() == 0) {
			errors.rejectValue("file", "uploadForm.selectFile",
					"Error! Please select a file!");
		}

	}

	@Override
	public boolean supports(Class<?> arg0) {
		return false;
	}

}
