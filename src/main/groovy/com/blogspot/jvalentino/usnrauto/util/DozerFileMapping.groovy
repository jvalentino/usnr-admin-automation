package com.blogspot.jvalentino.usnrauto.util

import org.dozer.CustomConverter;

/**
 * Utility for mapping a file to another file, since dozer can't to it by default
 * @author jvalentino2
 *
 */
class DozerFileMapping implements CustomConverter {

	@Override
	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {
		
			if (sourceFieldValue == null)
				return null
			
			File source = (File) sourceFieldValue
			
			return new File(source.getAbsolutePath())
		
	}

	
	
}
