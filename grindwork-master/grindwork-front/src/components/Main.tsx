import Api from "../api/Api";
import Home from "./Home";
import Search from "./Search";
import { useEffect, useState } from "react";
import Swal from 'sweetalert2'

const Main = () => {
  const [anuncio, setAnuncio] = useState<any[]>([]);
  const [search, setSearch] = useState("");
  const [localidade, setLocalidade] = useState("");

  const getAnuncios = async (searchTerm = "", localidadeTerm = "") => {
    try {
      const response = await Api.get(
        `/anuncio/listaAnuncioPaginado?page=0&servico=${searchTerm}&localidade=${localidadeTerm}`
      );
      setAnuncio(response.data.content || []);
    } catch (error: any) {
      console.error("ops! ocorreu um erro: " + error);
      Swal.fire({
        title:"Erro ao listar anúncios!",
        text: `${error.response.data.msg}`,
        icon: "error"
      });
    }
  };

  useEffect(() => {
    getAnuncios();
  }, []);

  const handleSearch = () => {
    getAnuncios(search, localidade);
  };

  return (
    <>
      <Search
        setSearch={setSearch}
        setLocalidade={setLocalidade}
        onSearch={handleSearch}
      />
      <Home anuncios={anuncio} />
    </>
  );
};

export default Main;
