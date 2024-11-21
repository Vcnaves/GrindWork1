import { Key, useState, useEffect } from "react";
import { Link } from "react-router-dom";
import Api from "../api/Api";
import defaultImg from "../assets/default-profile.jpg";

const MeusAnuncios = () => {
  const [userData, setUserData] = useState({
    anuncio: [],
    data_nascimento: "",
    email: "",
    endereco: "",
    id: "",
    imagemUrl: "",
    localidade: { id: null, cidade: "", estado: "" },
    nome: "",
    senha: "",
    telefone: "",
  });
  const [anuncio, setAnuncio] = useState<any[]>([]);

  useEffect(() => {
    const data = JSON.parse(localStorage.getItem("dadosUsuario") || "[]")[0];
    if (data) {
      setUserData({
        anuncio: data.anuncio || [],
        data_nascimento: data.data_nascimento || "",
        email: data.email || "",
        endereco: data.endereco || "",
        id: data.id || "",
        imagemUrl: data.imagemUrl || "",
        localidade: {
          id: data.localidade?.id || "",
          cidade: data.localidade?.cidade || "",
          estado: data.localidade?.estado || "",
        },
        nome: data.nome || "",
        senha: data.senha || "",
        telefone: data.telefone || "",
      });
    }
  }, []);

  const getAnuncios = async (usuarioId = "") => {
    if (!usuarioId) return;
    try {
      const response = await Api.post(
        `/anuncio/meusAnuncioPaginado?usuarioId=${usuarioId}`
      );
      setAnuncio(response.data.content || []);
    } catch (err: any) {
      console.error("ops! ocorreu um erro: " + err);
    }
  };

  useEffect(() => {
    if (userData?.id) {
      getAnuncios(userData.id);
    }
  }, [userData]);

  return (
    <div className="max-w-md mx-auto mt-10 p-6">
      <h2 className="text-2xl font-bold mb-4">Meus Anúncios</h2>
      {anuncio.length > 0 ? (
        anuncio.map(
          (
            ad: {
              id: number;
              imageUrl: string | undefined;
              titulo: string;
              descricao: string;
              preco: number;
              localidade: { id: ""; cidade: ""; estado: "" } | undefined;
            },
            id: Key | null | undefined
          ) => (
            <Link to={"/details"} state={{ data: ad }} key={ad.id}>
              <div
                key={id}
                className="flex flex-row md:flex-row w-full p-4 mb-4 bg-white shadow rounded cursor-pointer border hover:border-black active:border-gray-500 active:border-2 overflow-hidden"
              >
                <img
                  src={ad.imageUrl || defaultImg}
                  alt="Anúncio"
                  className="mb-2 rounded w-36 h-32 md:w-36 md:h-32 md:mr-3"
                />
                <div className="flex flex-col w-full ml-1 md:flex-row ">
                  <div className="flex flex-col w-full ml-1 md:w-full">
                    <h3 className="font-semibold text-lg break-words">
                      {ad.titulo}
                    </h3>
                    <p className="w-full break-words overflow-hidden">
                      {ad.descricao.length > 35
                        ? `${ad.descricao.substring(0, 30)}...`
                        : ad.descricao}
                    </p>
                    <p className="w-full break-words overflow-hidden">
                      {ad.localidade?.cidade},{ad.localidade?.estado}
                    </p>
                    <p className="text-gray-700 font-semibold">R$ {ad.preco}</p>
                  </div>
                </div>
              </div>
            </Link>
          )
        )
      ) : (
        <p>Nenhum anúncio disponível.</p>
      )}
    </div>
  );
};

export default MeusAnuncios;
