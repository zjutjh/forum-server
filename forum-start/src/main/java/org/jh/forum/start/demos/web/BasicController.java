/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jh.forum.start.demos.web;

import org.jh.forum.api.dubbo.CorrectDataService;
import org.jh.forum.api.dubbo.PublishPostReq;
import org.jh.forum.server.config.NacosConfigConfiguration;
import org.jh.forum.server.config.service.NacosConfigAService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
public class BasicController {
    @Resource
    private CorrectDataService correctDataService;

    // http://127.0.0.1:8080/hello?name=lisi
    @GetMapping("/hello")
    @ResponseBody
    public String hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
        correctDataService.publishPost(null);
        return "Hello " + name + ". I am " + NacosConfigAService.nacosConfigA.getName();
    }

    // http://127.0.0.1:8080/hello_1?name=lisi
    @GetMapping("/hello_1")
    @ResponseBody
    public String hello1(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
        correctDataService.publishPost(PublishPostReq.newBuilder().setContext("").setUid("2").build());
        return "Hello " + name + ". I am " + NacosConfigAService.nacosConfigA.getName();
    }

}
