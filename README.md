<h2 align="center"> Mini Redis </h2>

<p align="center">
Projeto de miniatura de um banco Redis em memória usando H2
</p>

<p align="center">
  <a href="https://github.com/HypThaNyx">
    <img alt="Made by Wesley Yago" src="https://img.shields.io/badge/made%20by-Wesley%20Yago-orange">
  </a>

  <img alt="Last Commit" src="https://img.shields.io/github/last-commit/HypThaNyx/miniredis">

  <img alt="Contributors" src="https://img.shields.io/github/contributors/HypThaNyx/miniredis">

  <img alt="License" src="https://img.shields.io/badge/license-MIT-orange">

  <a href="https://www.linkedin.com/in/wesley-yago-da-silva/">
    <img alt="Check out my LinkedIn!" src="https://img.shields.io/badge/-LinkedIn-black.svg?logo=linkedin&color=666">
  </a>
</p>

---

## 🗺 Tabela de conteúdos

<ul>
  <li><a href="#-como-funciona">Como funciona</a></li>
  <li><a href="#-testando-na-sua-máquina">Testando na sua máquina</a></li>
  <li><a href="#-features">Features</a></li>
  <li><a href="#-apoio">Apoio</a></li>
  <li><a href="#-licença">Licença</a></li>
</ul>

---

## 🧪 Como funciona

Essa aplicação se trata de uma REST API com o intuito de simular alguns dos comandos atualmente oferecidos pelo banco de
dados [Redis](https://redis.io/).

Através do consumo das rotas expostas pelo controller, o usuário é capaz de executar um comando por vez ao passar os
parâmetros exigidos pela rota.

A lista de todos os comandos oferecidos pelo Redis pode ser encontrada [aqui](http://redis.io/commands).

Como o propósito principal do Redis é armazenar valores, foi utilizado o H2 como banco de dados em memória.
Além do desenvolvimento da lógica e regra de negócio similares ao Redis, também foram criados testes unitários para
garantir o funcionamento das funções encontradas na camada de serviços.

Gostou e quer testar a aplicação na sua máquina? O tópico abaixo "Testando na sua máquina" pode te ajudar nisso.

---

## 🚀 Testando na sua máquina

*(Passo a passo de como baixar e executar o projeto em qualquer máquina)*

### Requisitos básicos

- [Git](https://git-scm.com/downloads)
- [JDK 18](https://www.oracle.com/java/technologies/javase/jdk18-archive-downloads.html)

### Instruções de uso

- escolha uma pasta e execute o Git Bash (clique direito na pasta -> Git Bash Here)

- clone esse repositório na sua máquina

``` bash
git clone https://github.com/HypThaNyx/miniredis.git
```

- entre na pasta do projeto

``` bash
cd miniredis
```

- rode o comando a seguir para rodar os testes unitários:

``` bash
mvn test
```

- para experimentar também é possível importar a Collection do Postman (encontrada na pasta do projeto), executar a
  aplicação e consumir as requests documentadas


- voilà!

---

## ⛳ Features

### Requisitos

- [X] Implementar TODOS os comandos requisitados, sendo eles:
  - [X] 1 - SET key value
  - [X] 2 - SET key value EX seconds
  - [X] 3 - GET key
  - [X] 4 - DEL key [key ...]
  - [X] 5 - DBSIZE
  - [X] 6 - INCR key
  - [X] 7 - ZADD key score member
  - [X] 8 - ZCARD key
  - [X] 9 - ZRANK key member
  - [X] 10 - ZRANGE key start stop
- [X] Fazer testes unitários
- [X] Código limpo

### Extras

- [X] Expor como API na porta 8080
- [X] Definir verbos HTTP
- [X] Comunicação por String ASCII

### Super extra

- [ ] ❓

---

### 📋 Documentação

Esse projeto segue as seguintes Commit Guidelines:

- build: Changes that affect the build system or external dependencies (example scopes: gulp, broccoli, npm)
- ci: Changes to our CI configuration files and scripts (example scopes: Travis, Circle, BrowserStack, SauceLabs)
- docs: Documentation only changes
- feat: A new feature
- fix: A bug fix
- perf: A code change that improves performance
- refactor: A code change that neither fixes a bug nor adds a feature
- style: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc)
- test: Adding missing tests or correcting existing tests
- chore: Misc content

---

## 📌 Apoio

Entre em contato e preste apoio através das seguintes redes:

- <a href="https://hypthanyx.itch.io/">
    <img alt="Check out my Itch.io!" src="https://img.shields.io/badge/Itch.io-HypThaNyx-fff?logo=itch.io&style=social">
  </a>
- <a href="https://twitter.com/hypthanyx">
    <img alt="Check out my Twitter!" src="https://img.shields.io/badge/Twitter-HypThaNyx-fff?logo=twitter&style=social">
  </a>
- <a href="https://www.instagram.com/hypthanyx/">
    <img alt="Check out my Instagram!" src="https://img.shields.io/badge/Instagram-HypThaNyx-fff?logo=instagram&style=social">
  </a>
- <a href="https://www.linkedin.com/in/wesley-yago-da-silva/">
    <img alt="Check out my LinkedIn!" src="https://img.shields.io/badge/LinkedIn-Wesley Yago-black.svg?logo=linkedin&color=666&style=social">
  </a>
- <a href="https://www.youtube.com/channel/UC_x5u0TqJWN4O3GMwZRWkrg">
    <img alt="Check out my YouTube!" src="https://img.shields.io/badge/YouTube-HypThaNyx-black.svg?logo=youtube&color=666&style=social">
  </a>

---

## 📝 Licença

<img alt="License" src="https://img.shields.io/badge/license-MIT-%2304D361">

Esse projeto está licenciado sob a licença do MIT - veja o arquivo de [LICENÇA](LICENSE) para mais detalhes.

---

🧰 Sendo desenvolvido por Wesley Yago!