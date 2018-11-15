package org.psg.like.anno;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.lang.reflect.Proxy;

public class DaoScanRegistar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;
    private ResourcePatternResolver resourcePatternResolver;
    private MetadataReaderFactory metadataReaderFactory;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(DaoScan.class.getName()));
        String[] basePackages = annoAttrs.getStringArray("basePackage");

        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver(resourceLoader);


        for (String basePackage : basePackages) {
            String path = "classpath:*" + basePackage.replaceAll("\\.", "/") + "/**/*.class";
            try {
                Resource[] resources = resourcePatternResolver.getResources(path);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader reader = metadataReaderFactory.getMetadataReader(resource);
                        if ( reader.getClassMetadata().isInterface() ) {
                            String className = reader.getClassMetadata().getClassName();
                            Class<?> clazz = resourceLoader.getClassLoader().loadClass(className);
                            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
                            beanDefinition.setBeanClass(clazz);
                            beanDefinition.setInstanceSupplier(() -> {
                                Object object = Proxy.newProxyInstance(resourceLoader.getClassLoader(), new Class[]{clazz}, new DaoProxy());
                                return object;
                            });
                            beanDefinition.setSynthetic(true);
                            beanDefinitionRegistry.registerBeanDefinition(className.substring(className.lastIndexOf(".") + 1), beanDefinition);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
