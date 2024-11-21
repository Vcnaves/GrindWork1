import Logo from "../assets/logo2.png";
import { IoClose } from "react-icons/io5";
import TextField from "./TextField";
import { SetStateAction, useState } from "react";
import Api from "../api/Api";
import Swal from "sweetalert2";
import { Link } from "react-router-dom";

interface Props {
  setLogginPop?: any;
  setRegisterPop?: any;
  setForgottPop?: any;
}

const Login = ({ setLogginPop, setRegisterPop, setForgottPop }: Props) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {
    if (!email || !password) {
      Swal.fire({
        title: "Atenção!",
        text: `Por favor, preencha todos os campos.`,
        icon: "warning",
      });
      return;
    }

    try {
      Swal.fire({
        didOpen: () => {
          Swal.showLoading();
        },
        allowOutsideClick: false,
      });
      const response = await Api.post("/login", {
        email: email,
        senha: password,
      });

      if (response.status === 200) {
        const data = response.data;
        localStorage.setItem("authToken", data.token);

        const dadosUser = await Api.post("/usuario/dadosUsuario", {
          email: email,
        });

        if (dadosUser.status === 200) {
          localStorage.setItem("dadosUsuario", JSON.stringify(dadosUser.data));

          if (dadosUser.data[0].authorities[0].authority == "ROLE_ADMIN") {
            const userManage = await Api.get("/admin/usuarios");

            localStorage.setItem("userManagement",JSON.stringify(userManage.data));
          }
          Swal.close();
          window.location.reload();
        } else {
          Swal.fire({
            title: "Atenção!",
            text: "Dados usuário não encontrado.\nTente novamente mais tarde.",
            icon: "warning",
          });
        }
      } else {
        Swal.fire({
          title: "Erro de Acesso!",
          text: "Credenciais inválidas. Tente novamente.",
          icon: "error",
        });
      }
    } catch (error: any) {
      console.error("Erro na requisição:", error);
      Swal.fire({
        title: "Erro de requisição!",
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
          className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity"
          aria-hidden="true"
        ></div>
        <div className="fixed inset-0 z-10 w-screen overflow-y-auto">
          <div className="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
            <div className="relative transform overflow-hidden rounded-lg bg-white text-left shadow-xl transition-all sm:my-8 sm:w-96 sm:max-w-lg">
              <div className="bg-white px-4 pb-4 pt-5 sm:p-6 sm:pb-4">
                <div
                  className="flex justify-end"
                  onClick={() => setLogginPop(false)}
                >
                  <IoClose className="size-8 text-slate-600 active:text-slate-400" />
                </div>

                <div className="sm:mt-0 sm:text-center">
                  <div className="flex flex-col items-center w-full">
                    <img
                      src={Logo}
                      alt="grindwork-logo"
                      className="w-30 h-10 m-2"
                    />
                    <p className="text-base font-medium text-gray-800 mb-5">
                      Transformando trabalho em resultados.
                    </p>

                    <div className="w-full">
                      <TextField
                        placeholder="Digite seu e-mail"
                        icon="user"
                        value={email}
                        onChange={(e: {
                          target: { value: SetStateAction<string> };
                        }) => setEmail(e.target.value)}
                      />
                      <TextField
                        placeholder="Digite sua senha"
                        icon="pwd"
                        value={password}
                        onChange={(e: {
                          target: { value: SetStateAction<string> };
                        }) => setPassword(e.target.value)}
                      />
                    </div>

                    <div className="w-full flex justify-start">
                      <h1
                        className="text-center mt-4 text-sm cursor-pointer hover:text-blue-800 active:text-blue-600"
                        onClick={() => {
                          setLogginPop(false);
                          setForgottPop(true);
                        }}
                      >
                        Esqueceu sua senha?
                      </h1>
                    </div>

                    <div className="w-full flex justify-end">
                      <div
                        onClick={handleLogin}
                        className="flex w-28 h-12 p-3 justify-center items-center cursor-pointer rounded-md border bg-white text-greenLogo hover:bg-greenLogo hover:text-white active:bg-orangeLogo focus:outline-none focus:ring focus:ring-white"
                      >
                        <h1 className="font-bold text-lg">Entrar</h1>
                      </div>
                    </div>

                    <div className="w-full flex justify-start mt-5 mb-5">
                      <h1
                        className="text-center mt-4 text-sm"
                        onClick={() => {
                          setLogginPop(false);
                          setRegisterPop(true);
                        }}
                      >
                        Não tem uma conta?{" "}
                        <span className="text-blue-800 cursor-pointer hover:text-gray-400 active:text-blue-600">
                          Crie agora mesmo!
                        </span>
                      </h1>
                    </div>

                    <div className="flex">
                      <h1 className="text-center mt-4 text-xs">
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
    </>
  );
};

export default Login;
