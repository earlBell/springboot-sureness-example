package com.usthe.sureness.sample.tom.sureness.config;

import com.usthe.sureness.matcher.DefaultPathRoleMatcher;
import com.usthe.sureness.matcher.PathTreeProvider;
import com.usthe.sureness.matcher.TreePathRoleMatcher;
import com.usthe.sureness.mgt.SurenessSecurityManager;
import com.usthe.sureness.processor.DefaultProcessorManager;
import com.usthe.sureness.processor.Processor;
import com.usthe.sureness.processor.ProcessorManager;
import com.usthe.sureness.processor.support.NoneProcessor;
import com.usthe.sureness.provider.annotation.AnnotationPathTreeProvider;
import com.usthe.sureness.sample.tom.sureness.processor.CustomTokenProcessor;
import com.usthe.sureness.sample.tom.sureness.provider.RedisAccountProvider;
import com.usthe.sureness.sample.tom.sureness.subject.CustomTokenSubjectCreator;
import com.usthe.sureness.subject.SubjectFactory;
import com.usthe.sureness.subject.SurenessSubjectFactory;
import com.usthe.sureness.subject.creater.NoneSubjectServletCreator;
import com.usthe.sureness.util.JsonWebTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * sureness config
 * @author tomsun28
 * @date 22:40 2020-03-02
 */
@Configuration
public class SurenessConfiguration {


    @Autowired
    private CustomTokenSubjectCreator customTokenSubjectCreator;

    @Bean
    CustomTokenProcessor customTokenProcessor(RedisAccountProvider redisAccountProvider){
        CustomTokenProcessor customTokenProcessor = new CustomTokenProcessor();
        customTokenProcessor.setRedisAccountProvider(redisAccountProvider);
        return customTokenProcessor;
    }

    @Bean
    ProcessorManager processorManager(CustomTokenProcessor customTokenProcessor) {
        // process init
        List<Processor> processorList = new LinkedList<>();
        // use default none processor
        NoneProcessor noneProcessor = new NoneProcessor();
        processorList.add(noneProcessor);
        // use custom token processor
        processorList.add(customTokenProcessor);
        return new DefaultProcessorManager(processorList);
    }

    /**
     * @param databasePathTreeProvider the path tree resource load from database
     */
    @Bean
    TreePathRoleMatcher pathRoleMatcher(PathTreeProvider databasePathTreeProvider) {
        // the path tree resource load form annotation - @RequiresRoles @WithoutAuth
        AnnotationPathTreeProvider annotationPathTreeProvider = new AnnotationPathTreeProvider();
        annotationPathTreeProvider.setScanPackages(Collections.singletonList("com.usthe.sureness.sample.tom.controller"));
        // pathRoleMatcher init
        DefaultPathRoleMatcher pathRoleMatcher = new DefaultPathRoleMatcher();
        //鉴权优先级
        pathRoleMatcher.setPathTreeProviderList(Arrays.asList(
                annotationPathTreeProvider,
                databasePathTreeProvider));
        pathRoleMatcher.buildTree();
        return pathRoleMatcher;
    }

    @Bean
    SubjectFactory subjectFactory() {
        // SubjectFactory init
        SubjectFactory subjectFactory = new SurenessSubjectFactory();
        subjectFactory.registerSubjectCreator(Arrays.asList(
                // attention! must add noSubjectCreator first
                new NoneSubjectServletCreator(),
                // use custom token creator
                customTokenSubjectCreator));
        return subjectFactory;
    }

    @Bean
    SurenessSecurityManager securityManager(ProcessorManager processorManager,
                                            TreePathRoleMatcher pathRoleMatcher, SubjectFactory subjectFactory) {
        // surenessSecurityManager init
        SurenessSecurityManager securityManager = SurenessSecurityManager.getInstance();
        securityManager.setPathRoleMatcher(pathRoleMatcher);
        securityManager.setSubjectFactory(subjectFactory);
        securityManager.setProcessorManager(processorManager);
        return securityManager;
    }

}
