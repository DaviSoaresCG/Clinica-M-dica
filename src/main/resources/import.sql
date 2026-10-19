INSERT INTO tb_pessoa (tipo_pessoa, nome, cpf, email, telefone, crm) VALUES ('MEDICO', 'Dra. Laura Alcântara', '11122233344', 'laura@medicina.com', '63991055232', '123456');
INSERT INTO tb_pessoa (tipo_pessoa, nome, cpf, email, telefone, crm) VALUES ('MEDICO', 'Dr. Carlos Eduardo', '22233344455', 'carlos@medicina.com', '63984001234', '654321');

INSERT INTO tb_pessoa (tipo_pessoa, nome, cpf, email, telefone, convenio) VALUES ('PACIENTE', 'Davi Soares', '33344455566', 'davi@email.com', '63992008888', 'Unimed');
INSERT INTO tb_pessoa (tipo_pessoa, nome, cpf, email, telefone, convenio) VALUES ('PACIENTE', 'Maria Oliveira', '44455566677', 'maria@email.com', '63981112222', 'Bradesco Saúde');

INSERT INTO tb_pessoa (tipo_pessoa, razao_social, cnpj, email, telefone) VALUES ('PJ', 'Hospital e Maternidade Vida S.A.', '12345678000199', 'contato@hospitalvida.com.br', '6332150000');

INSERT INTO tb_consulta (date, value, observation, medico_id, paciente_id) VALUES ('2026-09-15 14:30:00', 250.00, 'Consulta de rotina', 1, 3);
INSERT INTO tb_consulta (date, value, observation, medico_id, paciente_id) VALUES ('2026-09-16 10:00:00', 300.00, 'Retorno pós-exames', 2, 4);
