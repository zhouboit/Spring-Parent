package com.jonbore.groovy.sample

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.jonbore.groovy.util.IDGenerator

/**
 *
 * Spring-Parent the name of the current project
 * @author bo.zhou* @since 2021/10/25
 */

println(name)
println(time)
println(JSON.toJSONString(obj))
def object = new JSONObject(obj);
object.put("time", new Date())
object.put("id", IDGenerator.getID())
object.put("IDGEN", IDGenerator.getUniqueID())
println(JSON.toJSONString(object))