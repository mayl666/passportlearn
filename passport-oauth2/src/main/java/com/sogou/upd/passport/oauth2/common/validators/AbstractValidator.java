/**
 *       Copyright 2010 Newcastle University
 *
 *          http://research.ncl.ac.uk/smart/
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sogou.upd.passport.oauth2.common.validators;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.HttpConstant;
import com.sogou.upd.passport.oauth2.common.exception.OAuthProblemException;
import com.sogou.upd.passport.oauth2.common.OAuth;
import com.sogou.upd.passport.oauth2.common.utils.OAuthUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 *
 *
 *
 */
public abstract class AbstractValidator<T extends HttpServletRequest> implements OAuthValidator<T> {

    protected List<String> requiredParams = new ArrayList<String>();
    protected Map<String, String[]> optionalParams = new HashMap<String, String[]>();
    protected List<String> notAllowedParams = new ArrayList<String>();


    @Override
    public void validateMethod(T request) throws OAuthProblemException {
        String method = request.getMethod();
        if (!method.equals(HttpConstant.HttpMethod.POST) || !method.equals(HttpConstant.HttpMethod.GET)) {
            throw OAuthUtils.handleOAuthProblemException("Method not set to POST or GET.");
        }
    }

    @Override
    public void validateContentType(T request) throws OAuthProblemException {
        String contentType = request.getContentType();
        final String expectedContentType = HttpConstant.ContentType.URL_ENCODED;
        if (!OAuthUtils.hasContentType(contentType, expectedContentType)) {
            throw OAuthUtils.handleBadContentTypeException(expectedContentType);
        }
    }

    @Override
    public void validateRequiredParameters(T request) throws OAuthProblemException {
        Set<String> missingParameters = new HashSet<String>();
        for (String requiredParam : requiredParams) {
            String val = request.getParameter(requiredParam);
            if (Strings.isNullOrEmpty(val)) {
                missingParameters.add(requiredParam);
            }
        }
        if (!missingParameters.isEmpty()) {
            throw OAuthUtils.handleMissingParameters(missingParameters);
        }
    }

    @Override
    public void validateOptionalParameters(T request) throws OAuthProblemException {

        Set<String> missingParameters = new HashSet<String>();

        for (Map.Entry<String, String[]> requiredParam : optionalParams.entrySet()) {
            String paramName = requiredParam.getKey();
            String val = request.getParameter(paramName);
            if (!Strings.isNullOrEmpty(val)) {
                String[] dependentParams = requiredParam.getValue();
                if (!OAuthUtils.hasEmptyValues(dependentParams)) {
                    for (String dependentParam : dependentParams) {
                        val = request.getParameter(dependentParam);
                        if (Strings.isNullOrEmpty(val)) {
                            missingParameters.add(dependentParam);
                        }
                    }
                }
            }
        }

        if (!missingParameters.isEmpty()) {
            throw OAuthUtils.handleMissingParameters(missingParameters);
        }
    }

    @Override
    public void validateNotAllowedParameters(T request) throws OAuthProblemException {
        List<String> notAllowedParameters = new ArrayList<String>();
        for (String requiredParam : notAllowedParams) {
            String val = request.getParameter(requiredParam);
            if (!Strings.isNullOrEmpty(val)) {
                notAllowedParameters.add(requiredParam);
            }
        }
        if (!notAllowedParameters.isEmpty()) {
            throw OAuthUtils.handleNotAllowedParametersOAuthException(notAllowedParameters);
        }
    }

    @Override
    public void performAllValidations(T request) throws OAuthProblemException {
        this.validateContentType(request);
        this.validateMethod(request);
        this.validateRequiredParameters(request);
        this.validateOptionalParameters(request);
        this.validateNotAllowedParameters(request);
    }
}
