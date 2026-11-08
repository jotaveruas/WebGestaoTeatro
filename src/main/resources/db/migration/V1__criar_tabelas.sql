create sequence usuario_codigo_seq start with 1 increment by 1;
create sequence peca_codigo_seq start with 1 increment by 1;
create sequence sessao_codigo_seq start with 1 increment by 1;
create sequence inscricao_codigo_seq start with 1 increment by 1;

create table usuario (
    codigo bigint default nextval('usuario_codigo_seq') primary key,
    nome varchar(120) not null,
    login varchar(60) not null,
    email varchar(254) not null,
    senha varchar(255) not null,
    ativo boolean not null,
    constraint uk_usuario_login unique (login),
    constraint uk_usuario_email unique (email)
);
create table espectador (
    codigo bigint primary key,
    cpf varchar(11),
    telefone varchar(20),
    data_nascimento date,
    constraint uk_espectador_cpf unique (cpf),
    constraint fk_espectador_usuario foreign key (codigo) references usuario(codigo)
);
create table usuario_papel (
    codigo_usuario bigint not null,
    papel varchar(20) not null,
    constraint uk_usuario_papel unique (codigo_usuario, papel),
    constraint fk_usuario_papel_usuario foreign key (codigo_usuario) references usuario(codigo),
    constraint ck_usuario_papel check (papel in ('ADMIN', 'FUNCIONARIO', 'ESPECTADOR'))
);
create table peca (
    codigo bigint default nextval('peca_codigo_seq') primary key,
    nome varchar(160) not null,
    descricao varchar(4000),
    duracao_minutos integer,
    classificacao_etaria integer
);
create table sessao (
    codigo bigint default nextval('sessao_codigo_seq') primary key,
    versao bigint not null default 0,
    codigo_peca bigint not null,
    data date not null,
    horario time not null,
    local varchar(160) not null,
    capacidade integer not null,
    valor numeric(10,2) not null,
    constraint fk_sessao_peca foreign key (codigo_peca) references peca(codigo),
    constraint ck_sessao_capacidade check (capacidade > 0),
    constraint ck_sessao_valor check (valor >= 0)
);
create table inscricao (
    codigo bigint default nextval('inscricao_codigo_seq') primary key,
    data_inscricao timestamp not null,
    situacao varchar(20) not null,
    codigo_espectador bigint not null,
    codigo_sessao bigint not null,
    constraint fk_inscricao_espectador foreign key (codigo_espectador) references espectador(codigo),
    constraint fk_inscricao_sessao foreign key (codigo_sessao) references sessao(codigo),
    constraint ck_inscricao_situacao check (situacao in ('PENDENTE', 'CONFIRMADA', 'CANCELADA'))
);
create index idx_sessao_peca on sessao(codigo_peca);
create index idx_inscricao_espectador on inscricao(codigo_espectador);
create index idx_inscricao_sessao on inscricao(codigo_sessao);
