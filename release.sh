rm -rf target 2> /dev/null
VERSION=$(jq -r .version package.json)
ZIP="target/jpmobile-${VERSION}.tar.gz"
lein release &&
tar -zc -f $ZIP * --exclude $ZIP --exclude 'orig' \
  --exclude 'src/jpmobile/t*' --exclude 'target/*/' \
  --exclude 'project.clj'
grunt version::patch &&
lein vcs commit
