import { Link } from "react-router-dom";
import defaultImg from '../assets/default-profile.jpg';

interface Props {
  anuncios: any;
}

const Home = ({ anuncios }: Props) => {
  localStorage.setItem("anuncios", JSON.stringify(anuncios));
  return (
    <>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 p-5 bg-white gap-4">
        {anuncios?.length > 0 ? (
          anuncios.map((data: any) => {
            return (
              <Link to={"/details"} state={{ data: data }} key={data.id}>
                <div className="border border-spacing-1 p-4 ml-3 mt-3 max-w-full">
                  <img
                    src={data.imageUrl || defaultImg}
                    alt={data.titulo}
                    className="w-full h-auto "
                  />
                  <h1 className="font-bold text-xl break-words">
                    {data.titulo}
                  </h1>
                  <h1 className="font-semibold">R${data.preco}</h1>
                  <h1 className="truncate">{data.endereco}</h1>
                  <h1>
                    {data.localidade.cidade}, {data.localidade.estado}
                  </h1>
                </div>
              </Link>
            );
          })
        ) : (
          <p>No data available</p>
        )}
      </div>
    </>
  );
};

export default Home;
