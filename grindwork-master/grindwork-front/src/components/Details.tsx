import { useEffect, useState } from "react";
import {
  FaChevronLeft,
  FaEye,
  FaEyeSlash,
  FaWhatsapp,
  FaStar,
} from "react-icons/fa";
import { Link, useLocation } from "react-router-dom";
import SendMessage from "./SendMessage";
import Login from "./Login";
import Register from "./Register";
import Comentarios from "./Comentarios";
import defaultImg from "../assets/default-profile.jpg";
import Api from "../api/Api";
import Swal from "sweetalert2";

const Details = () => {
  const location = useLocation();

  const [showPhone, setShowPhone] = useState(false);
  const [sendMessage, setSendMessage] = useState(false);
  const [logginPop, setLogginPop] = useState(false);
  const [registerPop, setRegisterPop] = useState(false);
  const [showButtonEdit, setShowButtonEdit] = useState(false);
  const [userData, setUserData] = useState({ email: "", id: null, nome: "", authority: "", telefone: ""});

  const phoneNumber = location.state.data.usuario.telefone;
  const sanitizedPhoneNumber = phoneNumber.replace(/[\s()\-]/g, "");
  const message =
    "Olá, tudo bem? \n encontrei seu anúncio no GrindWork e gostaria de mais informações."; // Mensagem padrão

  const whatsappLink = `https://wa.me/${sanitizedPhoneNumber}?text=${encodeURIComponent(
    message
  )}`;

  // Função para verificar se o token existe no localStorage
  const isUserLoggedIn = () => {
    return !!localStorage.getItem("authToken"); // Verifica se o token está presente
  };

  // Função para exibir alerta ou redirecionar se o usuário não estiver logado
  const handleAuthValidation = (action: {
    (): void;
    (): void;
    (): void;
    (): void;
  }) => {
    if (!isUserLoggedIn()) {
      setLogginPop(true);
    } else {
      action();
    }
  };

  const togglePhone = () => {
    handleAuthValidation(() => {
      setShowPhone(!showPhone);
    });
  };

  const handleWhatsappClick = () => {
    handleAuthValidation(() => {
      window.open(whatsappLink, "_blank", "noopener noreferrer");
    });
  };

  const handleSendMessageClick = () => {
    handleAuthValidation(() => {
      setSendMessage(!sendMessage);
    });
  };

  const goBack = () => {
    window.history.back();
  };

  const handleDeletAnuncio = async () => {
    try {
      const delAnuncio = await Api.delete("/anuncio/excluirAnuncio", {
        data: { id: location.state.data.id },
      });
      if (delAnuncio) {
        Swal.fire({
          title: "Concluido!",
          text: `Anuncio excluido com sucesso!`,
          icon: "success",
        });
        window.history.back();
      }
    } catch (error:any) {
      console.error("Erro ao excluir anuncio: " + error);
      Swal.fire({
        title: `Erro ao excluir anuncio!`,
        text: error.response.data.msg,
        icon: "error",
      });
    }
  };

  useEffect(() => {
    const data = JSON.parse(localStorage.getItem("dadosUsuario") || "[]")[0];
    if (data) {
      setUserData({
        email: data.email || "",
        id: data.id || "",
        nome: data.nome || "",
        authority: data.authorities[0].authority || "",
        telefone: data.telefone || ""
      });
    }
  }, []);

  useEffect(() => {
    if (userData.id) {
      if ((location.state.data.usuario.id == userData.id) || userData.authority == 'ROLE_ADMIN') {
        setShowButtonEdit(true);
      }
    }
  }, [userData]);

  return (
    <>
      {logginPop && (
        <Login setLogginPop={setLogginPop} setRegisterPop={setRegisterPop} />
      )}
      {registerPop && (
        <Register setLogginPop={setLogginPop} setRegisterPop={setRegisterPop} />
      )}
      <div className="relative w-full lg:w-full bg-slate-200 z-0">
        <div className="flex w-full h-10 rounded-md items-center justify-between">
          <button
            className="flex text-slate-700 mr-2 hover:text-gray-400"
            onClick={goBack}
          >
            <FaChevronLeft className="mt-1.5 mr-1" />
            Voltar
          </button>
          {showButtonEdit && (
            <div className="flex w-42 flex-row items-center justify-end p-2 ">
             <Link to={"/cadastrar-anuncio"} state={{data:location.state.data}} key={location.state.data.id}>
              <button className="w-20 justify-center mr-1 bg-yellow-400 text-white hover:bg-yellow-500 active:bg-yellow-300">
                Editar
              </button>
             </Link>
              <button
                onClick={handleDeletAnuncio}
                className="w-20 justify-center ml-1 bg-red-400 text-white hover:bg-red-500 active:bg-red-300"
              >
                Excluir
              </button>
            </div>
          )}
        </div>
      </div>

      <div className="flex flex-col  bg-white p-4 rounded-sm ">
        <div className="flex flex-col md:flex-row justify-center mb-4">
          <div className=" md:w-96 rounded-sm overflow-hidden">
            <img
              src={location.state.data.imageUrl || defaultImg}
              alt={location.state.data.titulo}
              className="w-full h-auto "
            />
            <div className="flex flex-col md:flex-row justify-left mt-4">
              <div className="flex flex-col w-full bg-white p-4 rounded-sm border-t">
                <h3 className="font-bold">ENDEREÇO</h3>
                <p className="text-gray-700 mt-2">
                  {location.state.data.endereco}
                </p>
                <p>
                  {location.state.data.localidade.cidade},
                  {location.state.data.localidade.estado}
                </p>
              </div>
            </div>
          </div>

          <div className="md:w-1/3 flex flex-col justify-between ml-0 md:ml-4 mt-4 md:mt-0 border rounded-sm p-4">
            <div>
              <div className="flex justify-between">
                <h2 className="text-lg font-bold">
                  {location.state.data.titulo}
                </h2>
                <div className="flex">
                  {[...Array(5)].map((_, index) => (
                    <FaStar
                      key={index}
                      size={20}
                      color={
                        index + 1 <= location.state.data.nota
                          ? "#ffc107"
                          : "#e4e5e9"
                      }
                    />
                  ))}
                </div>
              </div>
              <p className="text-2xl font-semibold text-green-600">
                R$ {location.state.data.preco}
              </p>

              <div className="flex flex-col md:flex-row justify-left mt-4">
                <div className="flex flex-col  bg-white p-4 rounded-sm border-t">
                  <h3 className="font-bold">DESCRIÇÃO</h3>
                  <p className="text-gray-700 mt-2">
                    {location.state.data.descricao}
                  </p>
                </div>
              </div>

              <div className="text-center text-xl font-bold mt-4 border-t">
                <div className="mt-3">
                  {location.state.data.usuario.nome || "PRESTADOR DE SERVIÇO"}
                </div>
              </div>
              <div className="text-center text-lg font-semibold mb-5 mt-1">
                <span className="mr-2">
                  Contato:
                  {showPhone
                    ? location.state.data.usuario.telefone
                    : "***********"}
                </span>
                <button
                  onClick={togglePhone}
                  className="focus:outline-none mt-1"
                >
                  {showPhone ? <FaEyeSlash size={20} /> : <FaEye size={20} />}
                </button>
              </div>
              <div className="flex w-full">
                <button
                  onClick={handleWhatsappClick}
                  className="px-6 py-3 mt-4 flex w-full justify-center bg-green-700 text-white  rounded-lg hover:bg-green-900 active:bg-green-500 transition duration-300"
                >
                  <FaWhatsapp className="mr-2 w-6 h-6" />
                  Enviar Mensagem Whatsapp
                </button>
              </div>
              <button
                onClick={handleSendMessageClick}
                className="mt-4 w-full font-semibold bg-white text-green-800 py-2 px-4 rounded-lg border border-green-800 hover:bg-slate-300 active:bg-green-800 active:text-white transition duration-300"
              >
                Enviar Mensagem E-mail
              </button>
              {sendMessage && <SendMessage setSendMessage={setSendMessage} userName={userData.nome} userEmail={userData.email} userPhone={userData.telefone} />}
            </div>
          </div>
          <div className="ml-4">
            <Comentarios />
          </div>
        </div>
      </div>
    </>
  );
};

export default Details;
