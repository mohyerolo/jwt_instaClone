<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html> 
<head>
<meta charset="utf-8">
<title>로그인</title>
</head>
<link rel="shortcut icon" href="/images/insta.png">
<!-- Style -->
<link rel="stylesheet" href="/css/layout.css">
<link rel="stylesheet" href="https://pro.fontawesome.com/releases/v5.10.0/css/all.css"
        integrity="sha384-AYmEC3Yw5cVb3ZcuHtOA93w35dYTsvhLPVnYs9eStHfGJvOvKxVfELGroGkvsg+p" crossorigin="anonymous" />

<body>

<div class="container">
	<main class="loginMain">
		<section class="login">
			<article class="login__form__container">
				<div class="login_form">
					<h1>
						<img src="/images/instagram-logo.png" alt="인스타그램"  style="display: block; width: 300px; margin: 0 auto;">
					</h1>

					<form class="login__input" action="/auth/login" method="post">
						<input type="text" name="userName" placeholder="유저네임"> <input
							type="password" name="password" placeholder="비밀번호">
						<button type="submit">로그인</button>
					</form>

					<div class="login__horizon">
						<div class="br"></div>
						<div class="or">또는</div>
						<div class="br"></div>
					</div>

					<div class="login__naver">
						<a href="/oauth2/authorization/naver">
							<button style="display:flex; justify-content:center; margin:auto; border:none;">
								<i><img src="/images/btnG_naver.png" style="width:20px; height:20px; margin-right: 5px;"/></i> <span>Facebook으로
									로그인</span>
							</button>
						</a>
					</div>

                  </div>


					<div class="login__register">
						<span>계정이 없으신가요?</span> <a href="/auth/joinForm">가입하기</a>
					</div>
			</article>
		</section>
	</main>
</div>

</body>
</html>