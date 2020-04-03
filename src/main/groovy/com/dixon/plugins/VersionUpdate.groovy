package com.dixon.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class VersionUpdate implements Plugin<Project> {

    def targetPoint = ["comp_book", "comp_boox", "comp_web", "compservice", "ddbaseframework", "ddlibrary"]
    def contentAdd = ["api ('com.luojilab.dedao.component.boox:comp_book:",
                      "api ('com.luojilab.dedao.component.boox:comp_boox:",
                      "api ('com.luojilab.dedao.component.boox:comp_web:",
                      "api('com.luojilab.dedao.componentlib.boox:compservice:",
                      "api ('com.luojilab.dedao.componentlib.boox:ddbaseframework:",
                      "api('com.luojilab.dedao.componentlib.boox:ddlibrary:"]
    def contentAddEnd = "') {"
    def resMap

    @Override
    void apply(Project project) {
        project.task('versionUpdate') {
            doLast {
                def filePath = 'versionTemp'
                def file = new File(filePath)
                resMap = change(file.text)
                resMap.each {
                    // 输出转换后的字符串
                    println it
                }
                def fileOutPath = 'ddsdk/build.gradle'
                def fileOut = new File(fileOutPath)
                def set = resMap.keySet()

                def res = ''
                fileOut.each {
                    def tag = false
                    set.each { s ->
                        if (it.contains(s)) {
                            tag = true
                            def at = it.indexOf('api')
                            def versionString = it.substring(0, at) + resMap.get(s)
                            res = res + versionString + '\n'
                        }
                    }
                    if (!tag) {
                        res = res + it + '\n'
                    }
                }
                fileOut.write(res)
            }
        }
    }

    private Map<String, String> change(String origin) {
        def map = new HashMap()
        for (int i = 0; i < targetPoint.size(); i++) {
            if (origin.contains(targetPoint[i] + " 的aar版本为")) {
                int startIndex = origin.indexOf(targetPoint[i] + " 的aar版本为") + targetPoint[i].length() + 9
                int endIndex = startIndex + 21
                def temp = origin.substring(startIndex, endIndex)
                def value = contentAdd[i] + temp + contentAddEnd
                map.put(contentAdd[i], value)
            }
        }
        return map
    }
}
