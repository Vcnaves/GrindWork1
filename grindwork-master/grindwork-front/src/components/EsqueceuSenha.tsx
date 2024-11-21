import { SetStateAction, useState } from "react";
import Logo from "../assets/logo2.png";
import TextField from "./TextField";
import { IoClose } from "react-icons/io5";
import Swal from "sweetalert2";
import Api from "../api/Api";

interface Props {
  setForgottPop?: any;
}

const EsqueceuSenha = ({ setForgottPop }: Props) => {
  const [email, setEmail] = useState("");

  const handleSendNewPwd = async () => {
    if (!email) {
      Swal.fire({
        title: "Alerta!",
        text: `Por favor, digite seu email.`,
        icon: "warning",
      });
      return;
    }
    Swal.fire({
      didOpen: () => {
        Swal.showLoading();
      },
      allowOutsideClick: false,
    });
    try {
      await Api.post("/usuario/esqueceuSenha", { email: email });
      Swal.close();
      setForgottPop(false);
      Swal.fire({
        title: "Concluido!",
        text: "Em breve, você receberá um email com sua nova senha!",
        icon: "success",
      });
    } catch (error: any) {
      console.error("Erro ao enviar redefinição de senha! \n", error);
      Swal.fire({
        title: "Erro ao enviar redefinição de senha!",
        text: `${error.response.data.msg}`,
        icon: "error",
      });
    }
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
                  onClick={() => setForgottPop(false)}
                >
                  <IoClose className="size-8 text-slate-600 active:text-slate-400" />
                </div>

                <div className="sm:mt-0 sm:text-center">
                  <div className="flex flex-col items-center w-full">
                    <img
                      src={Logo}
                      alt="grindwork-logo"
                      className="w-30 h-10 m-2 mb-8"
                    />
                    <p className="text-base font-medium text-gray-800 mb-5">
                      Por favor, insira o e-mail da sua conta para que possamos
                      redefinir sua senha.
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
                    </div>
                    <div className="w-full flex justify-end mt-5">
                      <div
                        onClick={handleSendNewPwd}
                        className="flex w-28 h-12 p-3 justify-center items-center cursor-pointer rounded-md border bg-white text-greenLogo hover:bg-greenLogo hover:text-white active:bg-orangeLogo focus:outline-none focus:ring focus:ring-white"
                      >
                        <h1 className="font-bold text-lg">Enviar</h1>
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

export default EsqueceuSenha;
