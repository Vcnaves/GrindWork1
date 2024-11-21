import Logo from "../assets/logo2.png";
import { IoClose } from "react-icons/io5";
import TextField from "./TextField";
import Divider from "@mui/material/Divider";
import SelectField from "./SelectField";
import DateField from "./DateField";
import PhoneField from "./PhoneField";
import { useEffect, useState } from "react";
import Api from "../api/Api";
import Swal from "sweetalert2";
import { Link } from "react-router-dom";

interface Props {
  setRegisterPop: any;
  setLogginPop: any;
}

const Register = ({ setRegisterPop, setLogginPop }: Props) => {
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha] = useState("");
  const [dtNascimento, setDtNascimeto] = useState("");
  const [contato, setContato] = useState("");
  const [estados, setEstados] = useState<{ value: string; label: string }[]>(
    []
  );
  const [cidades, setCidades] = useState<{ value: string; label: string }[]>(
    []
  );
  const [estadoSelecionado, setEstadoSelecionado] = useState({
    value: "",
    label: "",
  });
  const [cidadeSelecionado, setCidadeSelecionado] = useState({
    value: "",
    label: "",
  });

  // Buscar estados
  useEffect(() => {
    fetch("https://servicodados.ibge.gov.br/api/v1/localidades/estados")
      .then((response) => response.json())
      .then((data) => {
        // Mapeando os dados para o formato correto
        const formattedStates = data
          .map((country: any) => ({
            value: country.id,
            label: country.nome,
          }))
          .sort(
            (
              a: { value: string; label: string },
              b: { value: string; label: string }
            ) => a.label.localeCompare(b.label)
          ); // Ordenando em ordem alfabética
        setEstados(formattedStates);
      })
      .catch((error) => {
        console.error("Erro ao carregar os estados:", error);
        Swal.fire({
          title: "Erro ao carregar os estados!",
          text: `${error.response.data.msg}`,
          icon: "error",
        });
      });
  }, []);

  // Buscar cidades quando um estado é selecionado
  useEffect(() => {
    const fetchCities = async () => {
      if (estadoSelecionado.value) {
        try {
          const response = await fetch(
            `https://servicodados.ibge.gov.br/api/v1/localidades/estados/${estadoSelecionado.value}/municipios`
          );
          const data = await response.json();

          // Mapeando os dados para o formato correto
          const formattedCities = data.map((country: any) => ({
            value: country.id,
            label: country.nome,
          }));
          setCidades(formattedCities); // Agora, cidades estão atualizadas
        } catch (error: any) {
          console.error("Erro ao carregar os cidades:", error);
          Swal.fire({
            title: "Erro ao carregar os cidades",
            text: `${error.response.data.msg}`,
            icon: "error",
          });
        }
      }
    };

    fetchCities();
  }, [estadoSelecionado]);

  // Função para enviar dados para a API
  const handleRegister = async () => {
    const userData = {
      nome: nome,
      data_nascimento: dtNascimento,
      telefone: contato,
      email: email,
      senha: senha || "",
      localidade: {
        estado: estadoSelecionado.label,
        cidade: cidadeSelecionado.label,
      },
    };
    try {
      Swal.fire({
      title: "Bem vindo!",
      text: "Estamos criando sua conta!",
      didOpen: () => {
        Swal.showLoading();
      },
      allowOutsideClick: false,
    });
      await Api.post("/usuario/cadastrarUsuario", userData);
      Swal.close();
      Swal.fire({
        title: "Usuário registrado com sucesso!",
        text: "Enviaremos por email sua senha de acesso!",
        icon: "success",
        confirmButtonText: "OK",
      }).then((result) => {
        if (result.isConfirmed) {
          window.location.reload();
        }
      });
    } catch (error: any) {
      console.error("Erro ao registrar o usuário:", error);
      Swal.fire({
        title: "Erro ao registrar o usuário",
        text: `${error.response.data.msg}`,
        icon: "error",
      });
    }
  };

  const closePopUp = () => {
    setLogginPop(false);
    setRegisterPop(false);
  };

  return (
    <>
      <div
        className="relative z-10"
        aria-labelledby="modal-title"
        role="dialog"
        aria-modal="true"
      >
        <div
          className="fixed inset-0 bg-gray-600 bg-opacity-75 transition-opacity"
          aria-hidden="true"
        ></div>
        <div className="fixed inset-0 z-10 w-screen overflow-y-auto">
          <div className="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
            <div className="relative transform overflow-hidden rounded-lg bg-white text-left shadow-xl transition-all sm:my-8 sm:w-96 sm:max-w-lg">
              <div className="bg-white px-4 pb-4 pt-5 sm:p-6 sm:pb-4">
                <div
                  className="flex justify-end"
                  onClick={() => setRegisterPop(false)}
                >
                  <IoClose className="size-8 text-slate-600 active:text-slate-400 p-0" />
                </div>

                <div className="sm:mt-0 sm:text-center">
                  <div className="flex flex-col w-full">
                    <div className="flex flex-col items-center">
                      <img
                        src={Logo}
                        alt="grindwork-logo"
                        className="w-20 h-10 ml-2"
                      />
                      <p className="text-base font-medium text-gray-800 mb-5">
                        Transformando trabalho em resultados.
                      </p>
                    </div>
                    <div className="w-full">
                      <Divider textAlign="center">Dados Usuário</Divider>
                      <TextField
                        placeholder="Nome Completo"
                        icon="dataUser"
                        value={nome}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                          setNome(e.target.value)
                        }
                      />
                      <DateField
                        onChange={(e) => setDtNascimeto(e.target.value)}
                      />
                      <PhoneField
                        onChange={(e) => setContato(e.target.value)}
                      />
                      <SelectField
                        placeholder="Selecione seu Estado"
                        options={estados}
                        onChange={(e) => {
                          const selectedOption = estados.find(
                            (option) =>
                              Number(option.value) === Number(e.target.value)
                          );
                          if (selectedOption) {
                            setEstadoSelecionado(selectedOption);
                          }
                        }}
                      />
                      {estadoSelecionado.value != "" && (
                        <SelectField
                          placeholder="Selecione sua Cidade"
                          options={cidades}
                          onChange={(e) => {
                            const selectedOption = cidades.find(
                              (option) =>
                                Number(option.value) === Number(e.target.value)
                            );
                            if (selectedOption) {
                              setCidadeSelecionado(selectedOption);
                            }
                          }}
                        />
                      )}
                      <Divider className="pt-5" textAlign="center">
                        Login
                      </Divider>
                      <TextField
                        placeholder="Digite seu E-mail"
                        icon="email"
                        value={email}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                          setEmail(e.target.value)
                        }
                      />
                      {/*<TextField
                        placeholder="Digite sua senha"
                        icon="pwd"
                        value={senha}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) =>
                          setSenha(e.target.value)
                        }
                      />*/}
                    </div>

                    <div className="w-full flex justify-end pt-5">
                      <div
                        className="flex w-full h-12 p-3 mt-3 justify-center items-center cursor-pointer rounded-md border-2  bg-greenLogo text-white hover:bg-white hover:text-greenLogo hover:border-greenLogo active:bg-orangeLogo focus:outline-none focus:ring focus:ring-white"
                        onClick={handleRegister}
                      >
                        <h1 className="font-bold text-lg">Criar conta</h1>
                      </div>
                    </div>

                    <div className="w-full flex justify-start mt-5 mb-5">
                      <h1
                        className="text-center mt-4 text-sm"
                        onClick={() => {
                          setRegisterPop(false);
                          setLogginPop(true);
                        }}
                      >
                        Já tem uma conta?{" "}
                        <span className="text-blue-800 cursor-pointer hover:text-gray-400 active:text-blue-600">
                          Entrar agora!
                        </span>
                      </h1>
                    </div>

                    <div className="flex">
                      <div className="w-full justify-center">
                        <h1 className="mt-4 text-xs">
                          Ao entrar na sua conta, você está aceitando os <br />
                          <Link
                            to="/termos"
                            className="text-blue-800 cursor-pointer hover:underline active:text-blue-600"
                            onClick={closePopUp}
                          >
                            Termos e Condições da Grind Work.
                          </Link>
                        </h1>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Register;
