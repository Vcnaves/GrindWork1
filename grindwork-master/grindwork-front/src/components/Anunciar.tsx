import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import defaultImg from "../assets/default-profile.jpg";
import LocalidadeComboBox from "./LocalidadeComboBox";
import Swal from "sweetalert2";
import Api from "../api/Api";

const Anunciar = () => {
  const location = useLocation();
  const [titulo, setTitulo] = useState("");
  const [descricao, setDescricao] = useState("");
  const [endereco, setEndereco] = useState("");
  const [valor, setValor] = useState("");
  const [imagem, setImagem] = useState<File | null>(null);
  const [imagemUrl, setImagemUrl] = useState<string>(defaultImg);
  const [localidadeSelecionada, setLocalidadeSelecionada] = useState<
    string | null
  >(null);
  const [showLocalidadeDropdown, setShowLocalidadeDropdown] = useState(false);
  const [userData, setUserData] = useState({ id: "", email: "" });
  const navigate = useNavigate();
  const hasLocationData = useMemo(() => location?.state?.data, [location]);

  useEffect(() => {
    const data = JSON.parse(localStorage.getItem("dadosUsuario") || "[]")[0];
    if (data) {
      setUserData({
        id: data.id || "",
        email: data.email || "",
      });
    }
  }, []);

  useEffect(() => {
    if (hasLocationData?.id) {
      setTitulo(hasLocationData?.titulo);
      setDescricao(hasLocationData?.descricao);
      setEndereco(hasLocationData?.endereco);
      setValor(hasLocationData?.preco);
      setImagemUrl(hasLocationData?.imageUrl || null);
      setLocalidadeSelecionada(
        `${hasLocationData?.localidade.cidade}, ${hasLocationData?.localidade.estado}`
      );
    }
  }, [hasLocationData?.id]);

  const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setImagem(file);
      const imagePreviewUrl = URL.createObjectURL(file);
      setImagemUrl(imagePreviewUrl);
    }
  };

  const handleSubmit = async (e: { preventDefault: () => void }) => {
    e.preventDefault();
    const formData = new FormData();
    formData.append("titulo", titulo);
    formData.append("descricao", descricao);
    formData.append("endereco", endereco);
    formData.append("valor", valor);
    formData.append("imagem", (imagem as File) || null);
    formData.append("localidade", localidadeSelecionada || "");
    
    if (!hasLocationData?.id) {
      formData.append("usuario_id", userData.id);
      try {
        Swal.fire({
          title: "Aguarde",
          text: "Estamos criando seu anúncio!",
          didOpen: () => {
            Swal.showLoading();
          },
          allowOutsideClick: false,
        });
        const criaAnuncio = await Api.post(
          "/anuncio/cadastrarAnuncio",
          formData,
          {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          }
        );
        if (criaAnuncio.status >= 200 && criaAnuncio.status < 300) {
          const dadosUser = await Api.post("/usuario/dadosUsuario", {
            email: userData.email,
          });

          localStorage.setItem(
            "dadosUsuario",
            JSON.stringify([dadosUser.data[0]])
          );
        }
        Swal.close()
        navigate("/meus-anuncios");
      } catch (error: any) {
        Swal.hideLoading()
        console.error("Erro ao cadastrar anúncio:", error);
        Swal.fire({
          title: "Erro ao cadastrar anúncio!",
          text: `${error.response.data.msg}`,
          icon: "error",
        });
      }
    } else {
      try {
        Swal.fire({
          title: "Aguarde",
          text: "Estamos atualizando seu anúncio!",
          didOpen: () => {
            Swal.showLoading();
          },
          allowOutsideClick: false,
        });
        formData.append("anuncio_id", hasLocationData?.id);

        const atualizarAnuncio = await Api.post(
          "/anuncio/atualizarAnuncio",
          formData,
          {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          }
        );
        if (atualizarAnuncio.status >= 200 && atualizarAnuncio.status < 300) {
          const dadosUser = await Api.post("/usuario/dadosUsuario", {
            email: userData.email,
          });

          localStorage.setItem(
            "dadosUsuario",
            JSON.stringify([dadosUser.data[0]])
          );
        }
        Swal.close();
        navigate("/meus-anuncios");
      } catch (error: any) {
        Swal.close();
        console.error("Erro ao atualizar anúncio:", error);
        Swal.fire({
          title: "Erro ao atualizar anúncio!",
          text: `${error.response.data.msg}`,
          icon: "error",
        });
      }
    }
  };

  const handleLocalidadeClick = () => {
    setShowLocalidadeDropdown((prev) => !prev);
  };

  return (
    <>
      <div className="flex w-full bg-slate-300">
        <h2 className="flex w-full text-2xl font-bold my-2 text-center justify-center">
          {!hasLocationData?.id ? "Criar Anúncio" : "Alterar Anuncio"}
        </h2>
      </div>
      <div className="flex flex-col bg-slate-200">
        <div className="flex bg-slate-200">
          <div className="max-w-5xl mx-auto mt-2 p-4 grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="flex justify-start col-span-1 min-w-[24rem] relative group">
              <img
                src={imagemUrl}
                alt="Preview"
                className="w-[24rem] h-[24rem] object-cover shadow-lg"
              />
              <input
                type="file"
                accept="image/*"
                onChange={handleImageUpload}
                className="hidden"
                id="imageUpload"
              />
              <label
                htmlFor="imageUpload"
                className="w-[24rem] h-[24rem] absolute top-0 left-0 bg-black bg-opacity-40 text-white text-sm flex items-center justify-center opacity-0 group-hover:opacity-100 cursor-pointer transition-opacity"
              >
                Alterar Imagem
              </label>
            </div>

            <div className="col-span-2 space-y-4 ml-auto">
              <form
                onSubmit={handleSubmit}
                className="space-y-4 grid grid-cols-1 md:grid-cols-2 gap-4"
              >
                <div className="col-span-2">
                  <label className="block mb-2 font-semibold">Título:</label>
                  <input
                    type="text"
                    value={titulo}
                    onChange={(e) => setTitulo(e.target.value)}
                    className="w-full px-4 py-3 border border-gray-300 rounded"
                    required
                  />
                </div>

                <div className="col-span-2">
                  <label className="block mb-2 font-semibold">Descrição:</label>
                  <textarea
                    value={descricao}
                    onChange={(e) => setDescricao(e.target.value)}
                    className="w-full px-4 py-3 border border-gray-300 rounded"
                    required
                  />
                </div>

                <div>
                  <label className="block mb-2 font-semibold">Valor:</label>
                  <input
                    type="number"
                    value={valor}
                    onChange={(e) => setValor(e.target.value)}
                    className="w-full px-4 py-3 border border-gray-300 rounded"
                    required
                  />
                </div>

                <div>
                  <label className="block mb-2 font-semibold">Endereço:</label>
                  <input
                    type="text"
                    value={endereco}
                    onChange={(e) => setEndereco(e.target.value)}
                    className="w-full px-4 py-3 border border-gray-300 rounded"
                    required
                  />
                </div>

                <div className="col-span-2">
                  <label className="block mb-2 font-semibold">
                    Localidade:
                  </label>
                  <div className="relative w-full lg:w-64">
                    <div className="flex w-full bg-slate-200 rounded-md items-center">
                      <div
                        className="flex w-full ml-2 cursor-pointer"
                        onClick={handleLocalidadeClick}
                      >
                        <input
                          type="text"
                          placeholder="Localidade"
                          className="w-full border h-12 p-2 bg-slate-200 rounded-md cursor-pointer"
                          value={localidadeSelecionada || ""}
                          readOnly
                        />
                      </div>
                    </div>
                    {showLocalidadeDropdown && (
                      <LocalidadeComboBox
                        onSelectLocalidade={(
                          estado: string,
                          cidade: string
                        ) => {
                          setLocalidadeSelecionada(`${cidade}, ${estado}`);
                          setShowLocalidadeDropdown(false);
                        }}
                      />
                    )}
                  </div>
                </div>

                <div className="col-span-2">
                  <button
                    type="submit"
                    className="w-full bg-green-900 text-white py-3 rounded hover:bg-green-950"
                  >
                    Salvar Anúncio
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Anunciar;
