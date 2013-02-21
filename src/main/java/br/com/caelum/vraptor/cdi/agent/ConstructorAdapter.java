package br.com.caelum.vraptor.cdi.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.ioc.SessionScoped;

//TODO unit tests
public class ConstructorAdapter {
	private static Map<String, CtClass> adaptedClasses = new HashMap<String, CtClass>();
	private final ClassPool classPool;
	private final ClassValidator classValidator = new ClassValidator();

	public ConstructorAdapter(ClassPool classPool) {
		this.classPool = classPool;

	}

	public CtClass tryToAddCDIConstructorFor(String className) {
		if (adaptedClasses.containsKey(className)) {
			return adaptedClasses.get(className);
		}
		try {
			CtClass ctClass = classPool.get(className);
			if (classValidator.isAValid(ctClass) && !thereIsNoArgsConstructor(ctClass)) {
				CtConstructor defaultConstructor = new CtConstructor(
						new CtClass[] {}, ctClass);
				defaultConstructor.setBody("{}");
				addInjectionAnnotationsForAllProviders(ctClass);
				ctClass.addConstructor(defaultConstructor);
			}
			return ctClass;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@SuppressWarnings("unchecked")
	private void addInjectionAnnotationsForAllProviders(CtClass ctClass)
			throws NotFoundException {
		CtConstructor constructor = getFirstConstructorWithArgs(ctClass);
		if (constructor != null) {
			// for some reasone, javassist needs both annotation. If i add one
			// by one, it can't keep all.
			add(ctClass, constructor,
					Arrays.asList(Inject.class, Autowired.class));
		}
	}

	private void add(
			CtClass ctClass,
			CtConstructor constructor,
			List<Class<? extends java.lang.annotation.Annotation>> annotationsToBeAdded) {
		ClassFile classFile = ctClass.getClassFile();
		ConstPool constPool = classFile.getConstPool();
		AnnotationsAttribute annotationsContext = new AnnotationsAttribute(
				constPool, AnnotationsAttribute.visibleTag);
		ArrayList<Annotation> annotations = new ArrayList<Annotation>(
				Arrays.asList(annotationsContext.getAnnotations()));
		for (Class<? extends java.lang.annotation.Annotation> annotation : annotationsToBeAdded) {
			Annotation injectAnnotation = new Annotation(
					annotation.getCanonicalName(), constPool);
			annotations.add(injectAnnotation);
		}
		annotationsContext.setAnnotations(annotations
				.toArray(new Annotation[] {}));
		constructor.getMethodInfo().addAttribute(annotationsContext);
	}

	private CtConstructor getFirstConstructorWithArgs(CtClass ctClass)
			throws NotFoundException {
		CtConstructor[] constructors = ctClass.getDeclaredConstructors();
		for (CtConstructor ctConstructor : constructors) {
			if (ctConstructor.getParameterTypes().length > 0) {
				return ctConstructor;
			}
		}
		return null;
	}

	private boolean thereIsNoArgsConstructor(CtClass ctClass)
			throws NotFoundException {
		CtConstructor[] constructors = ctClass.getDeclaredConstructors();
		for (CtConstructor ctConstructor : constructors) {
			if (ctConstructor.getParameterTypes().length == 0) {
				return true;
			}
		}
		return false;
	}

}
