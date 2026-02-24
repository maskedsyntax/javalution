# Maintainer: maskedsyntax <https://github.com/maskedsyntax>
pkgname=javalution
pkgver=1.0.0
pkgrel=1
pkgdesc="High-performance Conway’s Game of Life simulator in JavaFX"
arch=('any')
url="https://github.com/maskedsyntax/javalution"
license=('MIT')
depends=('java-runtime>=25')
makedepends=('gradle')
source=("https://github.com/maskedsyntax/javalution/archive/refs/tags/v$pkgver.tar.gz")
sha256sums=('SKIP') # Replace with actual SHA256 after tagging

build() {
  cd "$pkgname-$pkgver"
  ./gradlew build
}

package() {
  cd "$pkgname-$pkgver"
  install -Dm644 "build/libs/$pkgname-1.0-SNAPSHOT.jar" "$pkgdir/usr/share/java/$pkgname/$pkgname.jar"
  
  # Create a runner script
  mkdir -p "$pkgdir/usr/bin"
  echo -e "#!/bin/sh
java -jar /usr/share/java/$pkgname/$pkgname.jar "\$@"" > "$pkgdir/usr/bin/$pkgname"
  chmod +x "$pkgdir/usr/bin/$pkgname"
}
