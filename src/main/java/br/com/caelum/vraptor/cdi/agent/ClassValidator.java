package br.com.caelum.vraptor.cdi.agent;

import javassist.CtClass;

public class ClassValidator {

	public boolean isAValid(CtClass ctClass) {		
			return !ctClass.isAnnotation()
					&& !ctClass.isInterface()
					&& !ctClass.isEnum()
					&& !ctClass.isArray()
					&& hasVRaptorAnnotation(ctClass);
	}
	
	private boolean hasVRaptorAnnotation(CtClass ctClass) {
		for (Object object : ctClass.getAvailableAnnotations()) {
			if (object.toString().startsWith("@br.com.caelum.vraptor")) {
				return true;
			}
		}
		return false;
	}
	
}
