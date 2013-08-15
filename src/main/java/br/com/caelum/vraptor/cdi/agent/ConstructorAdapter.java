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
				addInjectionAnnotationsForAllProviders(ctClass);
				CtConstructor defaultConstructor = new CtConstructor(
						new CtClass[] {}, ctClass);
				defaultConstructor.setBody("{}");
				ctClass.addConstructor(defaultConstructor);
				return ctClass;
			}
			return null;
		} catch (Throwable e) {
			System.err.println(e);
			throw new RuntimeException(e);
		}

	}

	private void addInjectionAnnotationsForAllProviders(CtClass ctClass)
			throws NotFoundException {
		CtConstructor constructor = ctClass.getDeclaredConstructors()[0];
		add(ctClass, constructor, Arrays.asList("javax.inject.Inject"));
	}

	private void add(
			CtClass ctClass,
			CtConstructor constructor,
			List<String> annotationsToBeAdded) {
		ClassFile classFile = ctClass.getClassFile();
		ConstPool constPool = classFile.getConstPool();
		AnnotationsAttribute annotationsContext = new AnnotationsAttribute(
				constPool, AnnotationsAttribute.visibleTag);
		List<Annotation> annotations = new ArrayList<Annotation>(
				Arrays.asList(annotationsContext.getAnnotations()));

		for (String annotation : annotationsToBeAdded) {
			annotations.add(new Annotation(annotation, constPool));
		}

		annotationsContext.setAnnotations(annotations
				.toArray(new Annotation[] {}));
		constructor.getMethodInfo().addAttribute(annotationsContext);
	}

	private boolean thereIsNoArgsConstructor(CtClass ctClass)
			throws NotFoundException {
		CtConstructor[] constructors = ctClass.getDeclaredConstructors();
		for (CtConstructor ctConstructor : constructors) {
			if (ctConstructor.getSignature().startsWith("()")) {
				return true;
			}
		}
		return false;
	}

}
