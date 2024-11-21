import { useState } from "react";
import { IoClose } from "react-icons/io5";
import { useLocation } from "react-router-dom";
import Api from "../api/Api";
import Swal from "sweetalert2";

interface Props {
  setSendMessage: any;
  userName?: string;
  userEmail?: string;
  userPhone?: string;
}

const SendMessage = ({ setSendMessage, userName, userEmail, userPhone }: Props) => {
  const location = useLocation();
  const [name, setName] = useState(userName || '');
  const [email, setEmail] = useState(userEmail || '');
  const [phone, setPhone] = useState(userPhone || '');
  const [service] = useState(`${location.state.data.titulo}`);
  const [destinatario] = useState(`${location.state.data.usuario.email}`);
  const [message, setMessage] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSendMessage(false);
  
    try {
      await Api.post("/anuncio/enviarOrcamento", {
        clienteNome: name,
        clienteEmail: email,
        clienteTelefone: phone,
        clienteMensagem: message,
        nomeServico: service,
        destinatarioEmail: destinatario,
      });
      Swal.fire({
        title: "Concluido!",
        text: "Orçamento enviado com sucesso!",
        icon: "success",
      });
    } catch (error:any) {
      console.error("Erro ao enviar o orçamento:", error);
      Swal.fire({
        title: "Erro ao enviar orçamento!",
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
            <div className="relative transform overflow-hidden rounded-lg bg-white text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-lg">
              <div className="w-full max-w-lg bg-white p-8 rounded-lg shadow-lg">
                <div
                  className="flex justify-end"
                  onClick={() => setSendMessage(false)}
                >
                  <IoClose className="size-8 text-slate-600 active:text-slate-400" />
                </div>
                <h2 className="text-2xl font-semibold text-center mb-6">
                  Enviar Mensagem
                </h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                  {/* Nome */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700">
                      Nome
                    </label>
                    <input
                      type="text"
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      placeholder="Seu nome"
                      required
                    />
                  </div>

                  {/* Email */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700">
                      Email
                    </label>
                    <input
                      type="email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      placeholder="Seu email"
                      required
                    />
                  </div>

                  {/* Telefone */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700">
                      Telefone
                    </label>
                    <input
                      type="tel"
                      value={phone}
                      onChange={(e) => setPhone(e.target.value)}
                      className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      placeholder="Seu telefone"
                      required
                    />
                  </div>

                  {/* Serviço */}
                  <div>
                    <label className="block text-sm font-medium text-gray-700">
                      Serviço
                    </label>
                    <input
                      type="text"
                      value={service}
                      onChange={() => {}}
                      className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                      required
                    />
                  </div>

                  {/* Corpo Email */}
                  <div className="col-span-full">
                    <label
                      htmlFor="about"
                      className="block text-sm font-medium leading-6 text-gray-900"
                    >
                      Mensagem
                    </label>
                    <div >
                      <textarea
                        id="about"
                        name="about"
                        rows={3}
                        value={message}
                        placeholder=" Digite aqui sua mensagem..."
                        onChange={(e) => setMessage(e.target.value)}
                        className="block w-full rounded-md border py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-1 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                        required
                      />
                    </div>
                    <p className="my-5 text-sm leading-6 text-gray-600">
                      Por favor, descreva detalhadamente o serviço a ser
                      executado para que o anunciante possa elaborar o orçamento
                      correspondente.
                    </p>
                  </div>

                  {/* Botão de Agendar */}
                  <div className="text-center">
                    <button
                      type="submit"
                      className="px-6 py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition duration-300"
                    >
                      Enviar Email
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default SendMessage;
