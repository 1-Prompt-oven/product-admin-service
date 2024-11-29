package org.example.productadminservice.common.config;

import java.util.Base64;

import org.bson.types.Binary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class BinaryToStringConverter implements Converter<Binary, String> {
	@Override
	public String convert(Binary source) {
		if (source == null) {
			return null;
		}
		return Base64.getEncoder().encodeToString(source.getData());
	}
}