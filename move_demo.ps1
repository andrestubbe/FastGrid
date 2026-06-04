$filesToMove = @(
    "AnimationController.java",
    "CellRenderer.java",
    "GalleryPanel.java",
    "GalleryWheelAdpater.java",
    "InteractionController.java",
    "LayoutController.java",
    "Main.java"
)

$srcDir = "C:\Users\andre\Documents\2026-05-17-Work-FastJava\FastGrid\src\main\java\fastgrid"
$destDir = "C:\Users\andre\Documents\2026-05-17-Work-FastJava\FastGrid\examples\Demo\src\main\java\fastgrid\demo"

Remove-Item -Path "$destDir\Demo.java" -Force -ErrorAction Ignore

foreach ($file in $filesToMove) {
    $srcPath = "$srcDir\$file"
    $destPath = "$destDir\$file"
    
    if (Test-Path $srcPath) {
        Move-Item -Path $srcPath -Destination $destPath -Force
        
        $content = Get-Content -Path $destPath -Raw
        $content = $content -replace 'package fastgrid;', "package fastgrid.demo;`r`n`r`nimport fastgrid.*;"
        
        if ($file -eq "Main.java") {
            $content = $content -replace 'public class Main', 'public class Demo'
            Set-Content -Path $destPath -Value $content
            Rename-Item -Path $destPath -NewName "Demo.java" -Force
        } else {
            Set-Content -Path $destPath -Value $content
        }
    }
}
