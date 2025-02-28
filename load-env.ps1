# load-env.ps1 - .env 파일에서 환경변수 읽어오기 (Windows PowerShell 전용)

$envFile = ".env"

if (!(Test-Path $envFile)) {
    Write-Host "❌ .env 파일을 찾을 수 없습니다."
    exit 1
}

# .env 파일 내용 읽어서 환경변수로 설정
Get-Content $envFile | ForEach-Object {
    if ($_ -match "^\s*#") { return }  # 주석(#) 라인 스킵
    if ($_ -match "^\s*$") { return }  # 빈 줄 스킵
    $parts = $_ -split '=', 2          # 키=값으로 분리
    if ($parts.Length -eq 2) {
        $key = $parts[0].Trim()
        $value = $parts[1].Trim()
        [System.Environment]::SetEnvironmentVariable($key, $value)
    }
}

Write-Host "✅ 환경변수 로드 완료"
