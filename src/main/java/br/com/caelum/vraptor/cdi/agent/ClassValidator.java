package br.com.caelum.vraptor.cdi.agent;

import javassist.CtClass;
import javassist.NotFoundException;
import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.ioc.SessionScoped;

public class ClassValidator {

	public boolean isAValid(CtClass ctClass) {
		try {
			return !ctClass.isAnnotation()
					&& !ctClass.isInterface()
					&& !ctClass.isEnum()
					&& !ctClass.isArray()
					&& (hasVRaptorAnnotation(ctClass) || hasVRaptorAnnotation(ctClass.getSuperclass()));
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean hasVRaptorAnnotation(CtClass ctClass){
		if(ctClass == null) return false;
		return ctClass.hasAnnotation(Component.class)
				|| ctClass.hasAnnotation(Resource.class)
				|| ctClass.hasAnnotation(ApplicationScoped.class)
				|| ctClass.hasAnnotation(SessionScoped.class)
				|| ctClass.hasAnnotation(RequestScoped.class)
				|| ctClass.hasAnnotation(PrototypeScoped.class)
				|| ctClass.hasAnnotation(Convert.class) || ctClass
					.hasAnnotation(Intercepts.class);
	}
	
}
