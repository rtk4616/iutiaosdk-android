import os
from fabric.api import local

def update_iutiao():
    src = os.path.join(os.path.expanduser("~"),
            "Projects/iutiao/out/artifacts/iutiao_jar/iutiao.jar")
    dest = "libs/iutiao.jar"
    local('cp %s %s' % (src, dest))
    local('git ci -m "update iutiao jar" %s' % dest)

def merge_and_push():
    local('git checkout master')
    local('git merge dev')
    local('git push origin master')
    local('git push gitcafe master')
    local('git push gitcafe_yxy master')
    local('git checkout dev')
